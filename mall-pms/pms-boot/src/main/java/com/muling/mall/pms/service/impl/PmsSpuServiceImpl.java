package com.muling.mall.pms.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.cert.service.HttpApiClientBSN;
import com.muling.common.constant.GlobalConstants;
import com.muling.common.constant.RedisConstants;
import com.muling.common.enums.BusinessTypeEnum;
import com.muling.common.exception.BizException;
import com.muling.common.redis.utils.BusinessNoGenerator;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.mall.pms.common.constant.PmsConstants;
import com.muling.mall.pms.common.enums.StatusEnum;
import com.muling.mall.pms.common.enums.ViewTypeEnum;
import com.muling.mall.pms.converter.SkuConverter;
import com.muling.mall.pms.converter.SpuConverter;
import com.muling.mall.pms.enums.BindEnum;
import com.muling.mall.pms.mapper.PmsSpuMapper;
import com.muling.mall.pms.pojo.dto.CheckPriceDTO;
import com.muling.mall.pms.pojo.dto.SpuInfoDTO;
import com.muling.mall.pms.pojo.entity.PmsBrand;
import com.muling.mall.pms.pojo.entity.PmsSku;
import com.muling.mall.pms.pojo.entity.PmsSpu;
import com.muling.mall.pms.pojo.form.GoodsFormDTO;
import com.muling.mall.pms.pojo.query.SpuAdminPageQuery;
import com.muling.mall.pms.pojo.query.SpuPageQuery;
import com.muling.mall.pms.pojo.vo.GoodsPageVO;
import com.muling.mall.pms.pojo.vo.SkuVO;
import com.muling.mall.pms.service.IPmsBrandService;
import com.muling.mall.pms.service.IPmsSkuService;
import com.muling.mall.pms.service.IPmsSpuService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * 商品业务实现类
 *
 * @author <a href="mailto:xianrui0365@163.com">haoxr</a>
 * @date 2021/8/8
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PmsSpuServiceImpl extends ServiceImpl<PmsSpuMapper, PmsSpu> implements IPmsSpuService {

    private final IPmsSkuService skuService;

    private final IPmsBrandService brandService;

    private final BusinessNoGenerator businessNoGenerator;

    private final StringRedisTemplate redisTemplate;

    private final RedissonClient redissonClient;

    private final HttpApiClientBSN httpApiClientBSN;


    private List<GoodsPageVO> spuDetailEx(List<PmsSpu> pmsSpuList) {
        List<GoodsPageVO> result = SpuConverter.INSTANCE.po2voList(pmsSpuList);
        for (int i = 0; i < result.size(); i++) {
            PmsBrand pmsBrand = brandService.getBrandDetails(result.get(i).getBrandId());
            if (pmsBrand != null) {
                GoodsPageVO.BrandInfo brandInfo = new GoodsPageVO.BrandInfo();
                brandInfo.setSpuCount(1);
                brandInfo.setSellCount(1);
                brandInfo.setName(pmsBrand.getName());
                brandInfo.setSimpleDsp(pmsBrand.getSimpleDsp());
                brandInfo.setDsp(pmsBrand.getDsp());
                brandInfo.setPicUrl(pmsBrand.getPicUrl());
                result.get(i).setBrandInfo(brandInfo);
            }
            PmsBrand pmsPublish = brandService.getBrandDetails(result.get(i).getPublishId());
            if (pmsPublish != null) {
                GoodsPageVO.PublishInfo publishInfo = new GoodsPageVO.PublishInfo();
                publishInfo.setName(pmsPublish.getName());
                publishInfo.setPicUrl(pmsPublish.getPicUrl());
                publishInfo.setSimpleDsp(pmsPublish.getSimpleDsp());
                publishInfo.setDsp(pmsPublish.getDsp());
                result.get(i).setPublishInfo(publishInfo);
            }
        }
        return result;
    }

    ;

    @Override
    public List<GoodsPageVO> pageSpuDetails(SpuPageQuery spuPageQuery) {
        QueryWrapper<PmsSpu> queryWrapper = new QueryWrapper<PmsSpu>()
                .eq(spuPageQuery.getSubjectId() != null, "subject_id", spuPageQuery.getSubjectId())
                .eq(spuPageQuery.getType() != null, "type", spuPageQuery.getType())
                .eq("visible", ViewTypeEnum.VISIBLE)
                .orderBy(StrUtil.isNotBlank(spuPageQuery.getOrderBy()), spuPageQuery.isAsc(), spuPageQuery.getOrderBy())
                .orderByDesc("updated");
        IPage<PmsSpu> pmsSpuPage = this.baseMapper.selectPage(new Page(spuPageQuery.getPageNum(), spuPageQuery.getPageSize()), queryWrapper);
        List<PmsSpu> pmsSpuList = pmsSpuPage.getRecords();
//        JSONArray jsa = JSONArray.fromObject(pmsSpuList);
//        log.info("spu list", jsa.toString());
        List<GoodsPageVO> result = this.spuDetailEx(pmsSpuList);
        return result;
    }

    @Override
    public List<GoodsPageVO> listSpuBySubjectId(Long subjectId, Integer dev) {
        LambdaQueryWrapper<PmsSpu> wrapper = Wrappers.<PmsSpu>lambdaQuery()
                .eq(PmsSpu::getDev, dev)
                .eq(PmsSpu::getStatus, GlobalConstants.STATUS_YES)
                .orderByDesc(PmsSpu::getUpdated);
        List<PmsSpu> pmsSpus = this.baseMapper.selectList(wrapper);
        return SpuConverter.INSTANCE.po2voList(pmsSpus);
    }

    @Override
    public IPage<PmsSpu> listAdminSpuPage(SpuAdminPageQuery queryParams) {

        QueryWrapper<PmsSpu> wrapper = Wrappers.<PmsSpu>query()
                .eq(queryParams.getSubjectId() != null, "subject_id", queryParams.getSubjectId())
                .eq(queryParams.getType() != null, "type", queryParams.getType())
                .like(StrUtil.isNotBlank(queryParams.getName()), "name", queryParams.getName())
                .orderByDesc("updated");
        IPage page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), wrapper);
        return page;
    }

    @Override
    public List<GoodsPageVO> getSpuPage(SpuAdminPageQuery queryParams) {
        return null;
    }

    /**
     * 「移动端」获取商品详情
     *
     * @param spuId 商品ID
     * @return
     */
    @Override
    public GoodsPageVO getAppSpuDetail(Long spuId) {
        LambdaQueryWrapper<PmsSpu> wrapper = Wrappers.<PmsSpu>lambdaQuery()
                .eq(PmsSpu::getId, spuId)
                .eq(PmsSpu::getStatus, GlobalConstants.STATUS_YES);
        PmsSpu pmsSpu = this.getOne(wrapper);
        Assert.isTrue(pmsSpu != null, "商品不存在");
        GoodsPageVO goodsPageVO = SpuConverter.INSTANCE.po2Vo(pmsSpu);
        return goodsPageVO;
    }

    @Override
    public List<GoodsPageVO> getAppSpuDetails(List<Long> spuIds) {
        List<PmsSpu> pmsSpus = this.listByIds(spuIds);
//        for (int i = 0; i < pmsSpus.size(); i++) {
//            //
//            List<Long> tmp_spuIds = new ArrayList<>();
//            tmp_spuIds.add(pmsSpus.get(i).getId());
//            List<SkuVO> skuVOList = skuService.getAppSkuDetails(tmp_spuIds);
//            if (skuVOList.isEmpty() == false) {
//                SkuVO sku = skuVOList.get(0);
//                Integer alive = pmsSpus.get(i).getTotal() - sku.getStockNum();
//                pmsSpus.get(i).setPrice(sku.getPrice());
//                pmsSpus.get(i).setSales(alive);
//            }
//        }
        List<GoodsPageVO> result = this.spuDetailEx(pmsSpus);
        return result;
    }


    /**
     * 添加商品
     *
     * @param goods
     * @return
     */
    @Override
    @Transactional
    public boolean addGoods(GoodsFormDTO goods) {
        // SPU
        PmsSpu pmsSpu = SpuConverter.INSTANCE.formToPo(goods);
        Long id = businessNoGenerator.generateLong(BusinessTypeEnum.SPU);
        pmsSpu.setId(id);
        boolean result = this.save(pmsSpu);
        if (result) {
            //增加商品，设置redis的更新时间
            Duration between = Duration.between(LocalDateTime.now(), pmsSpu.getStarted());
            if (between.getSeconds() > 0) {
                redisTemplate.opsForValue().set(RedisConstants.PMS_SPU_START_PREFIX + pmsSpu.getId(), pmsSpu.getId() + "", between.getSeconds(), TimeUnit.SECONDS);
            }
        }
        return result;
    }

    /**
     * 修改商品
     *
     * @param goods
     * @return
     */
    @Transactional
    @Override
    public boolean updateGoods(Long id, GoodsFormDTO goods) {
        // SPU
        PmsSpu spu = getById(id);
        if (spu == null) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        SpuConverter.INSTANCE.updatePo(goods, spu);
        boolean result = this.updateById(spu);
        Assert.isTrue(result, "更新商品失败");
        //按秒来更新销售时间
        Duration between = Duration.between(LocalDateTime.now(), spu.getStarted());
        if (between.getSeconds() > 0) {
            redisTemplate.opsForValue().set(RedisConstants.PMS_SPU_START_PREFIX + spu.getId(), spu.getId() + "", between.getSeconds(), TimeUnit.SECONDS);
        } else {
            redisTemplate.delete(RedisConstants.PMS_SPU_START_PREFIX + spu.getId());
        }
        return result;
    }

    /**
     * 批量删除商品（SPU）
     *
     * @param spuIds
     * @return
     */
    @Override
    @Transactional
    public boolean removeByGoodsIds(List<Long> spuIds) {
        boolean result = true;
        for (Long spuId : spuIds) {
            // spu
            LambdaQueryWrapper<PmsSpu> wrapper = new LambdaQueryWrapper<PmsSpu>()
                    .eq(PmsSpu::getStatus, StatusEnum.DOWN)
                    .eq(PmsSpu::getBind, BindEnum.UN_BIND)
                    .eq(PmsSpu::getPublishStatus, 0)
                    .eq(PmsSpu::getMintStatus, 0)
                    .eq(PmsSpu::getId, spuId);
            result = this.remove(wrapper);
            Assert.isTrue(result, "删除商品失败");
            result = skuService.remove(new LambdaQueryWrapper<PmsSku>().eq(PmsSku::getSpuId, spuId));
        }
        return result;
    }

    @Override
    public List<PmsSpu> listAll() {
        return this.baseMapper.listAll();
    }

    @Override
    public SpuInfoDTO getSpuInfo(Long spuId) {
        PmsSpu spu = getById(spuId);
        SpuInfoDTO spuInfo = SpuConverter.INSTANCE.po2dto(spu);
        return spuInfo;
    }

    /**
     * 商品发布锁定
     */
    @GlobalTransactional
    @Override
    public SpuInfoDTO publishLock(Long spuId) {
        PmsSpu spu = getById(spuId);
        spu.setPublishStatus(1);
        boolean ret = this.updateById(spu);
        if (ret) {
            SpuInfoDTO spuInfo = SpuConverter.INSTANCE.po2dto(spu);
            return spuInfo;
        }
        return null;
    }

    /**
     * 商品发布解开锁定
     */
    @GlobalTransactional
    @Override
    public boolean publishUnlock(Long spuId, boolean isOk) {
        PmsSpu spu = getById(spuId);
        if (isOk) {
            spu.setPublishStatus(2);
        } else {
            spu.setPublishStatus(0);
        }
        boolean ret = this.updateById(spu);
        return ret;
    }

    /**
     * 商品发布揭开锁定
     */
    @Override
    public boolean saleChange(Long spuId, Integer count) {
        //
        boolean reUpdate = false;
        RLock lock = redissonClient.getLock(PmsConstants.LOCK_SPU_SALE_NUM_PREFIX + spuId);
        try {
            lock.lock();
            PmsSpu pmsSpu = this.getById(spuId);
            pmsSpu.setSales(pmsSpu.getSales() + count);
            reUpdate = this.updateById(pmsSpu);
        } finally {
            //释放锁
            if (lock.isLocked()) {
                lock.unlock();
            }
        }
        return reUpdate;
    }

    /**
     * 商品验价
     *
     * @param checkPriceDTO
     * @return
     */
    @Override
    public boolean checkPrice(CheckPriceDTO checkPriceDTO) {
        // 订单总金额
        Long orderTotalAmount = checkPriceDTO.getOrderTotalAmount();
        // 计算商品总金额
        List<CheckPriceDTO.CheckSku> checkOrderItems = checkPriceDTO.getCheckSkus();
        if (CollectionUtil.isNotEmpty(checkOrderItems)) {
            List<Long> spuIds = checkOrderItems.stream()
                    .map(orderItem -> orderItem.getSpuId()).collect(Collectors.toList());
            List<PmsSpu> spuList = this.list(new LambdaQueryWrapper<PmsSpu>().in(PmsSpu::getId, spuIds)
                    .select(PmsSpu::getId, PmsSpu::getPrice));
            // 商品总金额
            Long spuTotalAmount = checkOrderItems.stream().map(checkOrderItem -> {
                Long spuId = checkOrderItem.getSpuId();
                PmsSpu pmsSpu = spuList.stream().filter(spu -> spu.getId().equals(spuId)).findFirst().orElse(null);
                if (pmsSpu != null) {
                    return pmsSpu.getPrice() * checkOrderItem.getCount();
                }
                return 0L;
            }).reduce(0L, Long::sum);
            if (spuTotalAmount == 0L) {
                return false;
            }
            return orderTotalAmount.compareTo(spuTotalAmount) == 0;
        }
        return false;
    }

    public static void main(String[] args) {

//        GoodsFormDTO.SkuFormDTO skuFormDTO = new GoodsFormDTO.SkuFormDTO();
//        skuFormDTO.setId(1L);
//        skuFormDTO.setName("测试");
//        skuFormDTO.setPrice(100L);
//        skuFormDTO.setSpuId(1L);
//        skuFormDTO.setStockNum(100);
//
//        PmsSpu spu = new PmsSpu();
//        spu.setId(1L);
//        PmsSku pmsSku = SkuConverter.INSTANCE.form2po(skuFormDTO);
//        System.out.println(pmsSku);
//
//
//        LocalDateTime end = LocalDateTime.of(2022, 6, 28, 19, 50, 0);
//
//        LocalDateTime start = LocalDateTime.now();
//
//
//        Duration duration2 = Duration.between(start, end);
//        System.out.println(duration2.getSeconds());
    }
}
