package com.muling.mall.bms.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.base.IBaseEnum;
import com.muling.common.constant.GlobalConstants;
import com.muling.common.constant.RedisConstants;
import com.muling.common.exception.BizException;
import com.muling.common.result.IResultCode;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.common.util.DateUtils;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.bms.constant.OmsConstants;
import com.muling.mall.bms.converter.ExchangeConfigConverter;
import com.muling.mall.bms.enums.*;
import com.muling.mall.bms.mapper.ExchangeConfigMapper;
import com.muling.mall.bms.pojo.entity.OmsExchangeConfig;
import com.muling.mall.bms.pojo.entity.OmsExchangeLog;
import com.muling.mall.bms.pojo.entity.OmsItemLog;
import com.muling.mall.bms.pojo.entity.OmsMemberItem;
import com.muling.mall.bms.pojo.form.admin.ExchangeConfigForm;
import com.muling.mall.bms.pojo.form.app.ExchangeForm;
import com.muling.mall.bms.pojo.query.admin.ExchangePageQuery;
import com.muling.mall.bms.pojo.vo.app.ExchangeVO;
import com.muling.mall.bms.service.IExchangeConfigService;
import com.muling.mall.bms.service.IExchangeLogService;
import com.muling.mall.bms.service.IItemLogService;
import com.muling.mall.bms.service.IMemberItemService;
import com.muling.mall.pms.api.SkuFeignClient;
import com.muling.mall.pms.api.SpuFeignClient;
import com.muling.mall.pms.enums.BindEnum;
import com.muling.mall.pms.pojo.dto.SpuInfoDTO;
import com.muling.mall.wms.api.WalletFeignClient;
import com.muling.mall.wms.enums.WalletOpTypeEnum;
import com.muling.mall.wms.pojo.dto.WalletDTO;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
@Service
public class ExchangeConfigServiceImpl extends ServiceImpl<ExchangeConfigMapper, OmsExchangeConfig> implements IExchangeConfigService {

    private final RedissonClient redissonClient;
    private final SkuFeignClient skuFeignClient;
    private final SpuFeignClient spuFeignClient;
    private final IItemLogService itemLogService;
    private final WalletFeignClient walletFeignClient;
    private final StringRedisTemplate redisTemplate;
    private final IMemberItemService memberItemService;
    private final IExchangeLogService exchangeLogService;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public IPage<ExchangeVO> page(ExchangePageQuery queryParams) {

        LambdaQueryWrapper<OmsExchangeConfig> wrapper = Wrappers.<OmsExchangeConfig>lambdaQuery()
                .eq(queryParams.getSpuId() != null, OmsExchangeConfig::getSpuId, queryParams.getSpuId())
                .eq(queryParams.getExchangeType() != null, OmsExchangeConfig::getExchangeType, queryParams.getExchangeType());
        if (queryParams.getStatus() != null) {
            wrapper.eq(OmsExchangeConfig::getStatus, IBaseEnum.getEnumByValue(queryParams.getStatus(), StatusEnum.class));
        }
        wrapper.eq(OmsExchangeConfig::getVisible, ViewTypeEnum.VISIBLE);
        wrapper.orderByDesc(OmsExchangeConfig::getUpdated);

        IPage page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), wrapper);

        List<ExchangeVO> list = ExchangeConfigConverter.INSTANCE.po2voList(page.getRecords());

        return page.setRecords(list);
    }

    @Override
    public boolean save(ExchangeConfigForm configForm) {
        OmsExchangeConfig config = ExchangeConfigConverter.INSTANCE.form2po(configForm);
        boolean b = this.save(config);
        if (!b) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        return b;
    }

    @Override
    public boolean updateById(Long id, ExchangeConfigForm configForm) {
        OmsExchangeConfig config = getById(id);
        if (config == null) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        ExchangeConfigConverter.INSTANCE.updatePo(configForm, config);

        return updateById(config);
    }


    @GlobalTransactional(rollbackFor = Exception.class)
    public boolean exchange(ExchangeForm exchangeForm) {
        //记得加锁
        Long memberId = MemberUtils.getMemberId();
        RLock lock = redissonClient.getLock(OmsConstants.ITEM_EXCHANGE_PREFIX + memberId);

        try {
            lock.lock();

            OmsExchangeConfig config = getById(exchangeForm.getExchangeId());
            Assert.isTrue(config != null, "兑换配置不存在");
            Assert.isTrue(config.getStatus().equals(StatusEnum.ENABLE), "兑换配置不可用");

            int num = 0;
            int maxNum = 0;

            if (config.getExchangeType() == ExchangeTypeEnum.COIN_TO_ITEM) {
                //币物兑换
                Long spuId = config.getSpuId();
                Result<SpuInfoDTO> spuInfo = spuFeignClient.getSpuInfo(spuId);
                Assert.isTrue(Result.isSuccess(spuInfo), "商品不存在");
                if(spuInfo.getData().getType() == 3 && false) {
                    //如果是工作包, 走工作包逻辑
                    Integer wpCount = memberItemService.itemCount(spuInfo.getData().getId(),spuInfo.getData().getType());
                    Assert.isTrue(wpCount.intValue() == 0, "已经兑换过");
                } else {
                    String memberExchangeNum = redisTemplate.opsForValue().get(OmsConstants.EXCHANGE_ITEM_NUM_PREFIX + memberId + ":" + exchangeForm.getExchangeId());
                    if (StrUtil.isNotBlank(memberExchangeNum)) {
                        num = Integer.parseInt(memberExchangeNum) + exchangeForm.getItemNum();
                    } else {
                        num = exchangeForm.getItemNum();
                    }
                    Assert.isTrue(num <= config.getPeriodValue(), "兑换次数超过上限，禁止兑换");

                    String maxLimit = redisTemplate.opsForValue().get(OmsConstants.EXCHANGE_MAX_LIMIT_PREFIX + exchangeForm.getExchangeId());
                    if (StrUtil.isNotBlank(maxLimit)) {
                        maxNum = Integer.parseInt(maxLimit) + exchangeForm.getItemNum();
                    } else {
                        maxNum = exchangeForm.getItemNum();
                    }
                    Assert.isTrue(maxNum < config.getMaxLimit(), "兑换次数超过上限，禁止兑换");
                }

                SpuInfoDTO spuInfoDTO = spuInfo.getData();
                Integer itemNum = exchangeForm.getItemNum();
                Assert.isTrue(itemNum >= 1, "兑换数量必须大于1");
//                Assert.isTrue(itemNum >= 1, "兑换数量必须大于1");
                Result<BigDecimal> oweValue = walletFeignClient.getCoinValueByMemberIdAndCoinType(memberId,config.getCoinType());
                Assert.isTrue(oweValue.getData()!=null,"钱包不存在");
                BigDecimal totalCoinValue = config.getCoinValue().multiply(new BigDecimal(itemNum));
                Assert.isTrue(oweValue.getData().compareTo(totalCoinValue)>=0,"余额不足");
                //兑换消耗
                WalletDTO walletDTO = new WalletDTO()
                        .setMemberId(memberId)
                        .setBalance(totalCoinValue.negate())
                        .setCoinType(config.getCoinType())
                        .setOpType(WalletOpTypeEnum.EXCHANGE_CONSUME.getValue())
                        .setRemark("兑换消耗");
                Result result = walletFeignClient.updateBalance(walletDTO);
                if (!Result.isSuccess(result)) {
                    throw new BizException(ResultCode.getValue(result.getCode()));
                }

                for (int i = 0; i < itemNum; i++) {
                    //兑换商品
                    long nanoTime = System.nanoTime();
                    Long increment = redisTemplate.opsForValue().increment(RedisConstants.OMS_ITEM_NO_SPU_PREFIX + spuInfo.getData().getId(), 1);

                    OmsMemberItem item = new OmsMemberItem()
                            .setMemberId(memberId)
                            .setItemNo(increment + "")
                            .setStatus(ItemStatusEnum.UN_MINT)
                            .setContract(spuInfoDTO.getContract())
//                            .setHexId(String.format("%064x", nanoTime))
                            .setName(spuInfoDTO.getName())
                            .setType(spuInfoDTO.getType())
                            .setBind(IBaseEnum.getEnumByValue(spuInfoDTO.getBind(), BindEnum.class))
                            .setPicUrl(spuInfoDTO.getPicUrl())
                            .setSpuId(spuInfoDTO.getId());
                    memberItemService.save(item);

                    OmsItemLog itemLog = new OmsItemLog()
                            .setMemberId(memberId)
                            .setMemberFrom(0L)
                            .setMemberTo(memberId)
                            .setSpuId(item.getSpuId())
//                            .setSkuId(item.getSkuId())
                            .setItemNo(item.getItemNo())
                            .setItemName(item.getName())
                            .setPicUrl(item.getPicUrl())
                            .setSourceUrl(item.getSourceUrl())
                            .setType(ItemLogTypeEnum.EXCHANGE)
                            .setReason("兑换：" + item.getName() + " " + config.getRemark());
                    itemLogService.save(itemLog);

                    //记录兑换记录
                    OmsExchangeLog log = new OmsExchangeLog()
                            .setExchangeType(config.getExchangeType().getValue())
                            .setMemberId(memberId)
                            .setSpuId(item.getSpuId())
                            .setItemName(item.getName())
                            .setItemNo(item.getItemNo())
                            .setPicUrl(item.getPicUrl())
                            .setCoinType(config.getCoinType())
                            .setCoinValue(config.getCoinValue())
                            .setRemark(config.getRemark());
                    exchangeLogService.save(log);

                    rabbitTemplate.convertAndSend("fanout.item.exchange", "", item.getId());
                }

            } else if (config.getExchangeType() == ExchangeTypeEnum.ITEM_TO_COIN) {
                //物币兑换
                String memberExchangeNum = redisTemplate.opsForValue().get(OmsConstants.EXCHANGE_ITEM_NUM_PREFIX + memberId + ":" + exchangeForm.getExchangeId());
                if (StrUtil.isNotBlank(memberExchangeNum)) {
                    num = Integer.parseInt(memberExchangeNum) + 1;
                } else {
                    num = 1;
                }
                Assert.isTrue(num < config.getPeriodValue(), "兑换次数超过上限，禁止兑换");

                String maxLimit = redisTemplate.opsForValue().get(OmsConstants.EXCHANGE_MAX_LIMIT_PREFIX + exchangeForm.getExchangeId());
                if (StrUtil.isNotBlank(maxLimit)) {
                    maxNum = Integer.parseInt(maxLimit) + 1;
                } else {
                    maxNum = 1;
                }
                Assert.isTrue(maxNum < config.getMaxLimit(), "兑换次数超过上限，禁止兑换");

                OmsMemberItem item = memberItemService.getById(exchangeForm.getItemId());
                Assert.isTrue(item != null, "物品不存在");
                Assert.isTrue(item.getMemberId().equals(memberId), "物品不属于该用户");

                //消耗物品
                item.setMemberId(GlobalConstants.ADMIN_MEMBER_ID);
                memberItemService.updateById(item);
                OmsItemLog itemLog = new OmsItemLog()
                        .setMemberId(memberId)
                        .setMemberFrom(memberId)
                        .setMemberTo(GlobalConstants.ADMIN_MEMBER_ID)
                        .setSpuId(item.getSpuId())
//                        .setSkuId(item.getSkuId())
                        .setItemNo(item.getItemNo())
                        .setItemName(item.getName())
                        .setPicUrl(item.getPicUrl())
                        .setType(ItemLogTypeEnum.EXCHANGE_CONSUME)
                        .setReason(ItemLogTypeEnum.EXCHANGE_CONSUME.getLabel());

                itemLogService.save(itemLog);
                //兑换接收
                WalletDTO walletDTO = new WalletDTO()
                        .setMemberId(memberId)
                        .setBalance(config.getCoinValue())
                        .setCoinType(config.getCoinType())
                        .setOpType(WalletOpTypeEnum.EXCHANGE_RECEIVE.getValue())
                        .setRemark("兑换接收");
                Result result = walletFeignClient.updateBalance(walletDTO);
                if (!Result.isSuccess(result)) {
                    throw new BizException(ResultCode.getValue(result.getCode()));
                }

                //记录兑换记录
                OmsExchangeLog log = new OmsExchangeLog()
                        .setExchangeType(config.getExchangeType().getValue())
                        .setMemberId(memberId)
                        .setSpuId(item.getSpuId())
                        .setItemName(item.getName())
                        .setItemNo(item.getItemNo())
                        .setPicUrl(item.getPicUrl())
                        .setCoinType(config.getCoinType())
                        .setCoinValue(config.getCoinValue())
                        .setRemark(config.getRemark());
                exchangeLogService.save(log);
            }

            //记数器
            if (config.getPeriodType() == 0) {
                Date date = new Date();
                long second = DateUtils.currentEndDayOfUnit(date, DateUnit.SECOND);
                redisTemplate.opsForValue().set(OmsConstants.EXCHANGE_ITEM_NUM_PREFIX + memberId + ":" + config.getId(), num + "", second, TimeUnit.SECONDS);
            } else {
                redisTemplate.opsForValue().set(OmsConstants.EXCHANGE_ITEM_NUM_PREFIX + memberId + ":" + config.getId(), num + "");
            }

            redisTemplate.opsForValue().set(OmsConstants.EXCHANGE_MAX_LIMIT_PREFIX + config.getId(), maxNum + "");
        } catch (BizException e) {
            IResultCode resultCode = e.getResultCode();
            log.error("{}.{}", resultCode.getCode(), resultCode.getMsg());
            throw e;
        } catch (Exception e) {
            log.error("", e);
            throw e;
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return true;
    }

    public static void main(String[] args) {

        Date date = new Date();
        long between = DateUtils.currentEndDayOfUnit(date, DateUnit.SECOND);
        System.out.println(between);

        Assert.isTrue(20 < 2, "兑换次数超过上限，禁止兑换");
    }
}
