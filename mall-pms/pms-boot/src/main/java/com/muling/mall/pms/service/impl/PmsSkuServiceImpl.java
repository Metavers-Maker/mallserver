package com.muling.mall.pms.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.muling.common.constant.RedisConstants;
import com.muling.common.enums.BusinessTypeEnum;
import com.muling.common.exception.BizException;
import com.muling.common.redis.utils.BusinessNoGenerator;
import com.muling.mall.pms.common.constant.PmsConstants;
import com.muling.mall.pms.common.enums.ViewTypeEnum;
import com.muling.mall.pms.converter.SkuConverter;
import com.muling.mall.pms.mapper.PmsSkuMapper;
import com.muling.mall.pms.pojo.dto.CheckPriceDTO;
import com.muling.mall.pms.pojo.dto.SkuInfoDTO;
import com.muling.mall.pms.pojo.dto.app.LockStockDTO;
import com.muling.mall.pms.pojo.entity.PmsSku;
import com.muling.mall.pms.pojo.entity.PmsSpu;
import com.muling.mall.pms.pojo.form.SkuForm;
import com.muling.mall.pms.pojo.form.UpdateSkuForm;
import com.muling.mall.pms.pojo.vo.SkuVO;
import com.muling.mall.pms.service.IPmsSkuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PmsSkuServiceImpl extends ServiceImpl<PmsSkuMapper, PmsSku> implements IPmsSkuService {

    private final StringRedisTemplate redisTemplate;
    private final RedissonClient redissonClient;

    private final BusinessNoGenerator businessNoGenerator;
    /**
     * 获取商品库存数量
     *
     * @param skuId
     * @return
     */
    @Override
    @Cacheable(cacheNames = "pms", key = "'stock_num:'+#skuId")
    public Integer getStockNum(Long skuId) {
        Integer stockNum = 0;
        PmsSku pmsSku = this.getOne(new LambdaQueryWrapper<PmsSku>()
                .eq(PmsSku::getId, skuId)
                .select(PmsSku::getStockNum));

        if (pmsSku != null) {
            stockNum = pmsSku.getStockNum();
        }
        return stockNum;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(SkuForm skuForm) {
        PmsSku pmsSku = SkuConverter.INSTANCE.form2po(skuForm);
        pmsSku.setTotalStockNum(skuForm.getStockNum());
        pmsSku.setTotalRndStockNum(skuForm.getRndStockNum());
        Long skuId = businessNoGenerator.generateLong(BusinessTypeEnum.SKU);
        pmsSku.setId(skuId);
        boolean result = this.save(pmsSku);
        if (result) {
            redisTemplate.delete(RedisConstants.PMS_SKU_STOCK_PREFIX + pmsSku.getId());
            redisTemplate.opsForValue().increment(RedisConstants.PMS_SKU_STOCK_PREFIX + pmsSku.getId(), pmsSku.getStockNum());
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean update(Long id, UpdateSkuForm skuForm) {
        PmsSku pmsSku = getById(id);
        SkuConverter.INSTANCE.updatePo(skuForm, pmsSku);
        boolean result = this.saveOrUpdate(pmsSku);
        if (result) {
            redisTemplate.delete(RedisConstants.PMS_SKU_STOCK_PREFIX + pmsSku.getId());
            redisTemplate.opsForValue().increment(RedisConstants.PMS_SKU_STOCK_PREFIX + pmsSku.getId(), pmsSku.getStockNum());
        }
        return result;
    }

    @Override
    public List<SkuVO> getAppSkuDetails(List<Long> spuIds) {
        LambdaQueryWrapper<PmsSku> wrapper = Wrappers.<PmsSku>lambdaQuery()
                .eq(PmsSku::getVisible, ViewTypeEnum.VISIBLE)
                .in(PmsSku::getSpuId, spuIds)
                .orderByAsc(PmsSku::getId);
        List<PmsSku> pmsSpus = this.list(wrapper);
        List<SkuVO> result = SkuConverter.INSTANCE.po2voList(pmsSpus);
        return result;
    }

    /**
     * 锁定库存 - 订单提交
     */
    @Override
    @Transactional
    public boolean lockStock(LockStockDTO lockStockDTO) {
        log.info("锁定商品库存:{}", JSONUtil.toJsonStr(lockStockDTO));

        List<LockStockDTO.LockedSku> lockedSkuList = lockStockDTO.getLockedSkuList();
        Assert.isTrue(CollectionUtil.isNotEmpty(lockedSkuList), "锁定的商品为空");

        // 循环遍历锁定商品
        lockedSkuList.forEach(lockedSku -> {
            RLock lock = redissonClient.getLock(PmsConstants.LOCK_SKU_PREFIX + lockedSku.getSkuId()); // 获取分布式锁
            // 加锁
            lock.lock();
            try {
                boolean lockResult = this.update(new LambdaUpdateWrapper<PmsSku>()
                        .setSql("locked_stock_num = locked_stock_num + " + lockedSku.getCount())
                        .eq(PmsSku::getId, lockedSku.getSkuId())
                        .apply("stock_num - locked_stock_num >= {0}", lockedSku.getCount())
                );
                Assert.isTrue(lockResult, "锁定商品 {} 失败", lockedSku.getSkuId());
            } finally {
                // 释放锁
                lock.unlock();
            }
        });

        // 将锁定的商品ID和对应购买数量持久化至Redis，后续使用场景: 1.订单取消归还库存;2.订单支付成功扣减库存。
        String orderToken = lockStockDTO.getOrderToken();
        redisTemplate.opsForValue().set(PmsConstants.LOCKED_STOCK_PREFIX + orderToken, JSONUtil.toJsonStr(lockedSkuList));

        // 无异常直接返回true
        return true;
    }

    /**
     * 释放库存 - 订单超时未支付
     */
    @Override
    public boolean unlockStock(String orderToken) {
        log.info("释放库存,orderToken:{}", orderToken);
        String lockedSkuJsonStr = redisTemplate.opsForValue().get(PmsConstants.LOCKED_STOCK_PREFIX + orderToken);
        List<LockStockDTO.LockedSku> lockedSkuList = JSONUtil.toList(lockedSkuJsonStr, LockStockDTO.LockedSku.class);
        lockedSkuList.forEach(item -> {
                    RLock lock = redissonClient.getLock(PmsConstants.LOCK_SKU_PREFIX + item.getSkuId()); // 获取分布式锁
                    // 加锁
                    lock.lock();
                    try {
                        this.update(new LambdaUpdateWrapper<PmsSku>()
                                .eq(PmsSku::getId, item.getSkuId())
                                .setSql("locked_stock_num = locked_stock_num - " + item.getCount()));
                    } finally {
                        // 释放锁
                        lock.unlock();
                    }
                }
        );

        // 删除redis中锁定的库存
        redisTemplate.delete(PmsConstants.LOCKED_STOCK_PREFIX + orderToken);
        return true;
    }

    /**
     * 扣减库存 - 支付成功
     */
    @Override
    public boolean deductStock(String orderToken) {
        log.info("扣减库存，orderToken:{}", orderToken);
        String lockedSkuJsonStr = redisTemplate.opsForValue().get(PmsConstants.LOCKED_STOCK_PREFIX + orderToken);
        List<LockStockDTO.LockedSku> lockedSkuList = JSONUtil.toList(lockedSkuJsonStr, LockStockDTO.LockedSku.class);

        lockedSkuList.forEach(item -> {
            RLock lock = redissonClient.getLock(PmsConstants.LOCK_SKU_PREFIX + item.getSkuId()); // 获取分布式锁
            // 加锁
            lock.lock();
            try {
                boolean result = this.update(new LambdaUpdateWrapper<PmsSku>()
                        .eq(PmsSku::getId, item.getSkuId())
                        .setSql("stock_num = stock_num - " + item.getCount())
                        .setSql("locked_stock_num = locked_stock_num - " + item.getCount())
                );
                if (!result) {
                    throw new BizException("扣减库存失败,商品" + item.getSkuId() + "库存不足");
                }
            } finally {
                // 释放锁
                lock.unlock();
            }
        });

        // 删除redis中锁定的库存
        redisTemplate.delete(PmsConstants.LOCKED_STOCK_PREFIX + orderToken);
        return true;
    }

    /**
     * 商品验价
     *
     * @param checkPriceDTO
     * @return
     */
    @Override
    public boolean checkPrice(CheckPriceDTO checkPriceDTO) {
        Long orderTotalAmount = checkPriceDTO.getOrderTotalAmount(); // 订单总金额
//        // 计算商品总金额
//        List<CheckPriceDTO.CheckSku> checkOrderItems = checkPriceDTO.getCheckSkus();
//        if (CollectionUtil.isNotEmpty(checkOrderItems)) {
//            List<Long> skuIds = checkOrderItems.stream()
//                    .map(orderItem -> orderItem.getSkuId()).collect(Collectors.toList());
//            List<PmsSku> skuList = this.list(new LambdaQueryWrapper<PmsSku>().in(PmsSku::getId, skuIds)
//                    .select(PmsSku::getId, PmsSku::getPrice));
//            // 商品总金额
//            Long skuTotalAmount = checkOrderItems.stream().map(checkOrderItem -> {
//                Long skuId = checkOrderItem.getSkuId();
//                PmsSku pmsSku = skuList.stream().filter(sku -> sku.getId().equals(skuId)).findFirst().orElse(null);
//                if (pmsSku != null) {
//                    return pmsSku.getPrice() * checkOrderItem.getCount();
//                }
//                return 0L;
//            }).reduce(0L, Long::sum);
//            if (skuTotalAmount == 0L) {
//                return false;
//            }
//            return orderTotalAmount.compareTo(skuTotalAmount) == 0;
//        }
        return false;
    }


    /**
     * 获取商品库存信息
     *
     * @param skuId
     * @return
     */
    @Override
    public SkuInfoDTO getSkuInfo(Long skuId) {
        MPJLambdaWrapper<PmsSku> wrapper = new MPJLambdaWrapper<PmsSku>()
                .selectAll(PmsSku.class)
                .selectAs(PmsSku::getId, "skuId")
                .selectAs(PmsSku::getClosed, "closed")
                .selectAs(PmsSku::getSpuId, "spuId")
                .selectAs(PmsSku::getName, "skuName")
                .selectAs(PmsSpu::getName, "spuName")
                .selectAs(PmsSpu::getPicUrl, "picUrl")
                .leftJoin(PmsSpu.class, PmsSpu::getId, PmsSku::getSpuId)
                .eq(PmsSku::getId, skuId);
        SkuInfoDTO skuInfo = this.baseMapper.selectJoinOne(SkuInfoDTO.class, wrapper);
        return skuInfo;
    }


    /**
     * 「实验室」修改商品库存数量
     *
     * @param skuId
     * @param stockNum 商品库存数量
     * @return
     */
    @Override
    public boolean updateStockNum(Long skuId, Integer stockNum) {
        boolean result = this.update(new LambdaUpdateWrapper<PmsSku>()
                .eq(PmsSku::getId, skuId)
                .set(PmsSku::getStockNum, stockNum)
        );
        return result;
    }

    /**
     * 扣减商品库存
     *
     * @param skuId
     * @param num   商品库存数量
     * @param num   商品盲盒开出来的库存数量
     * @return
     */
    @Override
    public boolean deductStock(Long skuId, Integer num, Integer rndNum, boolean deductTotal) {
        PmsSku pmsSku = getById(skuId);
        Assert.notNull(pmsSku, "Sku 没找到");
        Assert.isTrue(pmsSku.getStockNum() >= num, "扣减库存数量不能大于当前剩余的库存量");
        Assert.isTrue(pmsSku.getRndStockNum() >= rndNum, "扣减库存数量不能大于当前剩余的库存量");

        boolean orderSkuDeductResult = false;
        if (num > 0) {
            RLock orderSkuLock = redissonClient.getLock(PmsConstants.LOCK_SKU_PREFIX + skuId); // 获取分布式锁
            // 加锁
            orderSkuLock.lock();

            try {
                orderSkuDeductResult = this.update(deductTotal ? new LambdaUpdateWrapper<PmsSku>()
                        .setSql("total_stock_num = total_stock_num - " + num)
                        .setSql("stock_num = stock_num - " + num)
                        .eq(PmsSku::getId, skuId) : new LambdaUpdateWrapper<PmsSku>()
                        .setSql("stock_num = stock_num - " + num)
                        .eq(PmsSku::getId, skuId)
                );

                if (orderSkuDeductResult) {
                    redisTemplate.delete(RedisConstants.PMS_SKU_STOCK_PREFIX + skuId);
                    redisTemplate.opsForValue().decrement(RedisConstants.PMS_SKU_STOCK_PREFIX + skuId, num);
                }
            } finally {
                // 释放锁
                orderSkuLock.unlock();
            }
        }

        boolean rndDeductResult = false;
        if (rndNum > 0) {
            RLock rndSkuLock = redissonClient.getLock(PmsConstants.LOCK_SKU_RND_PREFIX + skuId); // 获取分布式锁
            // 加锁
            rndSkuLock.lock();

            try {
                rndDeductResult = this.update(deductTotal ? new LambdaUpdateWrapper<PmsSku>()
                        .setSql("total_rnd_stock_num = total_rnd_stock_num - " + rndNum)
                        .setSql("rnd_stock_num = rnd_stock_num - " + rndNum)
                        .eq(PmsSku::getId, skuId) : new LambdaUpdateWrapper<PmsSku>()
                        .setSql("rnd_stock_num = rnd_stock_num - " + rndNum)
                        .eq(PmsSku::getId, skuId)
                );

            } finally {
                // 释放锁
                rndSkuLock.unlock();
            }
        }

        return orderSkuDeductResult && rndDeductResult;
    }


    /**
     * 「新增商品库存
     *
     * @param skuId
     * @param num   商品库存数量
     * @return
     */
    @Override
    public boolean addStock(Long skuId, Integer num, Integer rndNum) {
        RLock orderSKuLock = redissonClient.getLock(PmsConstants.LOCK_SKU_PREFIX + skuId); // 获取分布式锁
        // 加锁
        orderSKuLock.lock();
        boolean orderSkuAddResult;
        try {
            orderSkuAddResult = this.update(new LambdaUpdateWrapper<PmsSku>()
                    .setSql("total_stock_num = total_stock_num + " + num)
                    .setSql("stock_num = stock_num + " + num)
                    .eq(PmsSku::getId, skuId)
            );

            if (orderSkuAddResult) {
                redisTemplate.delete(RedisConstants.PMS_SKU_STOCK_PREFIX + skuId);
                redisTemplate.opsForValue().increment(RedisConstants.PMS_SKU_STOCK_PREFIX + skuId, num);
            }
        } finally {
            // 释放锁
            orderSKuLock.unlock();
        }

        RLock rndSkuLock = redissonClient.getLock(PmsConstants.LOCK_SKU_RND_PREFIX + skuId); // 获取分布式锁
        // 加锁
        rndSkuLock.lock();
        boolean rndSkuAddResult;
        try {
            rndSkuAddResult = this.update(new LambdaUpdateWrapper<PmsSku>()
                    .setSql("total_rnd_stock_num = total_rnd_stock_num + " + rndNum)
                    .setSql("rnd_stock_num = rnd_stock_num + " + rndNum)
                    .eq(PmsSku::getId, skuId)
            );

        } finally {
            // 释放锁
            rndSkuLock.unlock();
        }
        return orderSkuAddResult && rndSkuAddResult;
    }

    /**
     * 增加铸造数量
     *
     * @param skuId
     * @return
     */
    @Override
    public boolean deductMint(Long skuId, Integer num) {
        boolean result = this.update(new LambdaUpdateWrapper<PmsSku>()
                .eq(PmsSku::getId, skuId)
                .setSql("mint_num = mint_num + " + num)
        );
        if (!result) {
            throw new BizException("增加铸造数失败,商品" + skuId + "铸造失败");
        }
        return true;
    }
}
