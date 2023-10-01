package com.muling.mall.bms.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
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
import com.muling.mall.bms.converter.CoinRewardConfigConverter;
import com.muling.mall.bms.converter.ExchangeConfigConverter;
import com.muling.mall.bms.enums.*;
import com.muling.mall.bms.mapper.CoinRewardConfigMapper;
import com.muling.mall.bms.mapper.ExchangeConfigMapper;
import com.muling.mall.bms.pojo.entity.*;
import com.muling.mall.bms.pojo.form.admin.CoinRewardConfigForm;
import com.muling.mall.bms.pojo.form.admin.ExchangeConfigForm;
import com.muling.mall.bms.pojo.form.app.ExchangeForm;
import com.muling.mall.bms.pojo.query.admin.CoinRewardPageQuery;
import com.muling.mall.bms.pojo.query.admin.ExchangePageQuery;
import com.muling.mall.bms.pojo.vo.app.CoinRewardVO;
import com.muling.mall.bms.pojo.vo.app.ExchangeVO;
import com.muling.mall.bms.service.*;
import com.muling.mall.pms.api.SkuFeignClient;
import com.muling.mall.pms.api.SpuFeignClient;
import com.muling.mall.pms.enums.BindEnum;
import com.muling.mall.pms.pojo.dto.SpuInfoDTO;
import com.muling.mall.wms.api.WalletFeignClient;
import com.muling.mall.wms.enums.WalletOpTypeEnum;
import com.muling.mall.wms.pojo.dto.WalletDTO;
import com.xxl.job.core.context.XxlJobHelper;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class CoinRewardConfigServiceImpl extends ServiceImpl<CoinRewardConfigMapper, BmsCoinConfig> implements ICoinRewardConfigService {

    private final RedissonClient redissonClient;

    private final IMemberItemService memberItemService;

    private final WalletFeignClient walletFeignClient;

    private final SpuFeignClient spuFeignClient;

    private final IItemLogService itemLogService;

    private final StringRedisTemplate redisTemplate;

    private final IExchangeLogService exchangeLogService;

    private final RabbitTemplate rabbitTemplate;

    @Override
    public IPage<CoinRewardVO> page(CoinRewardPageQuery queryParams) {

        LambdaQueryWrapper<BmsCoinConfig> wrapper = Wrappers.<BmsCoinConfig>lambdaQuery()
                .like(queryParams.getName() != null, BmsCoinConfig::getName, queryParams.getName())
                .eq(queryParams.getFromType() != null, BmsCoinConfig::getFromType, queryParams.getFromType());
        wrapper.eq(BmsCoinConfig::getVisible, ViewTypeEnum.VISIBLE);
        wrapper.orderByDesc(BmsCoinConfig::getUpdated);

        IPage page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), wrapper);

        List<CoinRewardVO> list = CoinRewardConfigConverter.INSTANCE.po2voList(page.getRecords());

        return page.setRecords(list);
    }

    @Override
    public boolean save(CoinRewardConfigForm configForm) {
        BmsCoinConfig config = CoinRewardConfigConverter.INSTANCE.form2po(configForm);
        boolean b = this.save(config);
        if (!b) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        return b;
    }

    @Override
    public boolean updateById(Long id, CoinRewardConfigForm configForm) {
        BmsCoinConfig config = getById(id);
        if (config == null) {
            throw new BizException(ResultCode.SYSTEM_EXECUTION_ERROR);
        }
        CoinRewardConfigConverter.INSTANCE.updatePo(configForm, config);

        return updateById(config);
    }

    /**
     * 释放持仓积分奖励
     */
    @GlobalTransactional
    private void publishCoinNum(List<OmsMemberItem> memberItemList, Map<FromTypeEnum, BmsCoinConfig> configMap) {
        //获取持仓时间
        List<WalletDTO> walletDTOS = Lists.newArrayList();
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        memberItemList.stream().forEach(memberItem -> {
            Duration between = Duration.between(memberItem.getStarted(), today);
            BmsCoinConfig coinConfig = configMap.get(memberItem.getFromType());
            if (coinConfig == null) {
                log.info("持仓配置不存在 {}", memberItem.getFromType().getLabel());
            } else {
                long toDay = between.toDays();
                if (toDay > 0) {
                    //更新持仓时间和奖励时间
                    memberItem.setStarted(today);
                    memberItem.setStickNum(memberItem.getStickNum() + 1);
                    boolean reUpdate = memberItemService.updateById(memberItem);
                    if (reUpdate) {
                        //计算L值奖励,
                        BigDecimal rewardBalance = coinConfig.getStickRate()
                                .multiply(BigDecimal.valueOf(toDay))
                                .multiply(BigDecimal.valueOf(memberItem.getSwapPrice().doubleValue() / 100));
                        WalletDTO walletDTO = new WalletDTO()
                                .setMemberId(memberItem.getMemberId())
                                .setBalance(rewardBalance)
                                .setCoinType(coinConfig.getCoinType())
                                .setOpType(WalletOpTypeEnum.STICK_REWARD.getValue())
                                .setRemark(WalletOpTypeEnum.STICK_REWARD.getLabel());
                        walletDTOS.add(walletDTO);
                        log.info("释放L值奖励:名字{}-编号{}-类型{}-奖励{}",
                                memberItem.getName(),
                                memberItem.getItemNo(),
                                coinConfig.getCoinType(),
                                rewardBalance);
                    }
                } else {
                    log.info("持仓时间不足:名字{}-编号{}-时间{}天",
                            memberItem.getName(),
                            memberItem.getItemNo(),
                            between.toDays());
                }
            }
        });
        //释放L值持仓奖励
        if (!walletDTOS.isEmpty()) {
            walletFeignClient.updateBalances(walletDTOS);
        }
        //
    }

    /**
     * 定时任务
     */
    @Override
    @Scheduled(cron = "0 0 1 * * ?")
    public void calculate() {
        log.info("[SCHEDULE] LValue reward start");
        RLock lock = redissonClient.getLock(OmsConstants.LVALUE_PUBLISH_PREFIX);
        try {
            lock.lock();
            //获取积分配置列表
            List<BmsCoinConfig> coinConfigs = this.list();
            Assert.isTrue(coinConfigs.size() != 0, "持仓奖励表未配置");
            Map<FromTypeEnum, BmsCoinConfig> configMap = coinConfigs.stream().collect(
                    Collectors.toMap(BmsCoinConfig::getFromType, Function.identity(), (key1, key2) -> key2)
            );
            //循环获取数据(test 持仓奖励)
            Integer pageNum = 1;
            Integer pageSize = 100;
            LambdaQueryWrapper<OmsMemberItem> queryWrapper = new LambdaQueryWrapper<OmsMemberItem>()
                    .ne(OmsMemberItem::getFreezeType, ItemFreezeTypeEnum.PUBLISH)
                    .orderByAsc(OmsMemberItem::getId);
            Page<OmsMemberItem> page = memberItemService.page(new Page(pageNum, pageSize), queryWrapper);
            if (page.getRecords().size() > 0) {
                this.publishCoinNum(page.getRecords(), configMap);
                while (page.hasNext()) {
                    pageNum = pageNum + 1;
                    page = memberItemService.page(new Page(pageNum, pageSize), queryWrapper);
                    this.publishCoinNum(page.getRecords(), configMap);
                }
            }
            //
        } catch (Exception e) {
            log.error("[SCHEDULE] LValue error occur", e);
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("[SCHEDULE] LValue unlock");
            }
            log.info("[SCHEDULE] LValue end");
        }
    }


    public static void main(String[] args) {
        //
    }
}
