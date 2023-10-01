package com.muling.mall.bms.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.constant.GlobalConstants;
import com.muling.common.exception.BizException;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.common.util.DoubleUtils;
import com.muling.mall.bms.constant.OmsConstants;
import com.muling.mall.bms.converter.FarmClaimConverter;
import com.muling.mall.bms.converter.FarmLogConverter;
import com.muling.mall.bms.enums.ItemFreezeStatusEnum;
import com.muling.mall.bms.enums.ItemFreezeTypeEnum;
import com.muling.mall.bms.enums.StatusEnum;
import com.muling.mall.bms.mapper.FarmClaimMapper;
import com.muling.mall.bms.pojo.dto.ClaimDTO;
import com.muling.mall.bms.pojo.dto.StakeDTO;
import com.muling.mall.bms.pojo.dto.WithdrawDTO;
import com.muling.mall.bms.pojo.entity.*;
import com.muling.mall.bms.pojo.query.app.FarmClaimPageQuery;
import com.muling.mall.bms.pojo.query.app.FarmLogPageQuery;
import com.muling.mall.bms.pojo.vo.app.FarmClaimVO;
import com.muling.mall.bms.pojo.vo.app.FarmLogVO;
import com.muling.mall.bms.service.*;
import com.muling.mall.wms.api.WalletFeignClient;
import com.muling.mall.wms.enums.WalletOpTypeEnum;
import com.muling.mall.wms.pojo.dto.WalletDTO;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class FarmServiceImpl implements IFarmService {

    private final RedissonClient redissonClient;

    private final IMemberItemService memberItemService;

    private final IFarmPoolService farmPoolService;
    private final IFarmStakeService farmStakeService;
    private final IFarmStakeItemService farmStakeItemService;
    private final IFarmLogService farmLogService;
    private final FarmClaimMapper farmClaimMapper;

    private final WalletFeignClient walletFeignClient;

    private final RabbitTemplate rabbitTemplate;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean stake(Long memberId, StakeDTO stakeDTO) {
        log.info("memberId:{}, stakeDTO:{}", memberId, stakeDTO);

        RLock lock = redissonClient.getLock(OmsConstants.ITEM_STAKE_PREFIX + stakeDTO.getPoolId());

        try {
            lock.lock();

            OmsMemberItem item = memberItemService.getById(stakeDTO.getItemId());
            Assert.isTrue(item != null, "itemId=" + stakeDTO.getItemId() + "+商品不存在");
            Assert.isTrue(item.getMemberId().equals(memberId), "物品不属于该用户");
            Assert.isTrue(item.getFreeze() != ItemFreezeStatusEnum.FREEZE, "物品已冻结");

            OmsFarmPool stakeConfig = farmPoolService.getById(stakeDTO.getPoolId());
            Assert.isTrue(stakeConfig != null, "挖矿配置不存在");

            List<OmsFarmPool.Rule> rules = JSONUtil.toList(stakeConfig.getData(), OmsFarmPool.Rule.class);
            Assert.isTrue(rules.size() > 0, "挖矿配置不存在");
            //从rules获得days的值
            OmsFarmPool.Rule rule = rules.stream().filter(r -> r.getDays().equals(stakeDTO.getDays())).findFirst().orElseThrow(() -> new BizException("挖矿锁仓天数规则不存在"));
            Assert.isTrue(rule != null, "挖矿锁仓天数规则不存在");

            //添加锁仓记录及系数
            farmStakeService.stake(item, stakeDTO, rule);

            //更新池总系数
            double totalAllocPoint = DoubleUtils.add(stakeConfig.getTotalAllocPoint(), rule.getAllocPoint());
            farmPoolService.updateById(stakeConfig.setTotalAllocPoint(totalAllocPoint));

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean withdraw(Long memberId, WithdrawDTO withdrawDTO) {
        log.info("memberId:{}, withdrawDTO:{}", memberId, withdrawDTO);

        RLock lock = redissonClient.getLock(OmsConstants.ITEM_STAKE_PREFIX + withdrawDTO.getPoolId());

        try {
            lock.lock();

            OmsFarmStakeItem stakeMemberItem = farmStakeItemService.getById(withdrawDTO.getStakeItemId());
            Assert.isTrue(stakeMemberItem != null, "质押记录不存在");
            Assert.isTrue(stakeMemberItem.getMemberId().equals(memberId), "质押记录不属于该用户");
            Assert.isTrue(withdrawDTO.getPoolId().longValue() == stakeMemberItem.getPoolId().longValue(), "池和质押记录不匹配");
            Assert.isTrue(stakeMemberItem.getDays() == stakeMemberItem.getCurrentDays(), "时间不到，不能提取");

            OmsMemberItem item = memberItemService.getById(stakeMemberItem.getItemId());
            Assert.isTrue(item != null, "物品不存在");
            Assert.isTrue(item.getMemberId().equals(memberId), "物品不属于该用户");
            Assert.isTrue(item.getFreeze() == ItemFreezeStatusEnum.FREEZE, "物品未冻结");
            Assert.isTrue(item.getFreezeType() == ItemFreezeTypeEnum.FARM, "物品未挖矿冻结");

            OmsFarmPool stakeConfig = farmPoolService.getById(stakeMemberItem.getPoolId());
            Assert.isTrue(stakeConfig != null, "挖矿配置不存在");

            //提取锁仓物品及系数
            OmsFarmStakeItem withdraw = farmStakeService.withdraw(item, stakeMemberItem);

            //更新池总系数
            double totalAllocPoint = DoubleUtils.sub(stakeConfig.getTotalAllocPoint(), withdraw.getAllocPoint());
            farmPoolService.updateById(stakeConfig.setTotalAllocPoint(totalAllocPoint));

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean settlePool(Long poolId) {
        log.info("poolId:{}", poolId);

        RLock lock = redissonClient.getLock(OmsConstants.ITEM_DISPATCH_PREFIX + poolId);

        try {
            lock.lock();
            OmsFarmPool stakeConfig = farmPoolService.getById(poolId);
            Assert.isTrue(stakeConfig != null, "挖矿配置不存在");
            Assert.isTrue(stakeConfig.getStatus() == StatusEnum.ENABLE, "挖池不可用");

            List<OmsFarmStake> list = farmStakeService.list(Wrappers.<OmsFarmStake>lambdaQuery().eq(OmsFarmStake::getPoolId, poolId));
            if (list == null || list.size() <= 0) {
                return true;
            }
            for (OmsFarmStake item : list) {

                Double min = DoubleUtils.divide(stakeConfig.getBalance().doubleValue(), stakeConfig.getTotalAllocPoint(), 1);
                Double result = DoubleUtils.mul(min, item.getAllocPoint());

                int currentDay = item.getCurrentDays() + 1;

                OmsFarmClaim stakeReward = new OmsFarmClaim()
                        .setPoolId(stakeConfig.getId())
                        .setMemberId(item.getMemberId())
                        .setCurrentDays(currentDay)
                        .setRewardAmount(new BigDecimal(result));
                farmClaimMapper.insert(stakeReward);

                item.setCurrentDays(currentDay);
                farmStakeService.updateById(item);
            }

            Integer days = stakeConfig.getDays();
            Integer currentDays = stakeConfig.getCurrentDays() + 1;
            if (days.intValue() <= currentDays.intValue()) {
                stakeConfig.setCurrentDays(days);
                stakeConfig.setStatus(StatusEnum.DISABLED);
            } else {
                stakeConfig.setCurrentDays(currentDays);
                stakeConfig.setBalance(stakeConfig.getDayAmount());
            }
            farmPoolService.updateById(stakeConfig);

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

    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public boolean claim(Long memberId, ClaimDTO claimDTO) {
        log.info("memberId:{}, claimDTO:{}", memberId, claimDTO);

        RLock lock = redissonClient.getLock(OmsConstants.ITEM_CLAIM_PREFIX + claimDTO.getStakeClaimId());

        try {
            lock.lock();

            OmsFarmClaim claim = farmClaimMapper.selectById(claimDTO.getStakeClaimId());
            Assert.isTrue(claim != null, "质押记录不存在");
            Assert.isTrue(claim.getStatus() == StatusEnum.ENABLE, "奖励已领取");
            Assert.isTrue(memberId.longValue() == claim.getMemberId().longValue(), "不是你的奖励不能领取");

            WalletDTO walletDTO = new WalletDTO()
                    .setMemberId(claim.getMemberId())
                    .setCoinType(claim.getCoinType())
                    .setBalance(claim.getRewardAmount())
                    .setOpType(WalletOpTypeEnum.STAKE_CLAIM.getValue())
                    .setRemark("质押奖励");
            Result result = walletFeignClient.updateBalance(walletDTO);
            if (!Result.isSuccess(result)) {
                throw new BizException(ResultCode.getValue(result.getCode()));
            }

            claim.setStatus(StatusEnum.DISABLED);
            farmClaimMapper.updateById(claim);

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

    @Override
    public void settle() {
        List<OmsFarmPool> list = farmPoolService.list(StatusEnum.ENABLE);
        for (OmsFarmPool pool : list) {
            String input = JSON.toJSONString(pool);
            rabbitTemplate.convertAndSend(GlobalConstants.MQ_FARM_SETTLE_QUEUE, input);
        }
    }

    @Override
    public IPage<FarmClaimVO> claimPage(Long memberId, FarmClaimPageQuery queryParams) {
        LambdaQueryWrapper<OmsFarmClaim> wrapper = Wrappers.<OmsFarmClaim>lambdaQuery()
                .eq(OmsFarmClaim::getMemberId, memberId)
                .eq(queryParams.getPoolId() != null, OmsFarmClaim::getPoolId, queryParams.getPoolId())
                .orderByDesc(OmsFarmClaim::getCreated);
        ;
        IPage page = farmClaimMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), wrapper);


        List<FarmClaimVO> list = FarmClaimConverter.INSTANCE.po2voList(page.getRecords());

        return page.setRecords(list);
    }

    @Override
    public IPage<FarmLogVO> logPage(Long memberId, FarmLogPageQuery queryParams) {
        LambdaQueryWrapper<OmsFarmLog> wrapper = Wrappers.<OmsFarmLog>lambdaQuery()
                .eq(OmsFarmLog::getMemberId, memberId)
                .eq(queryParams.getPoolId() != null, OmsFarmLog::getPoolId, queryParams.getPoolId())
                .orderByDesc(OmsFarmLog::getCreated);
        ;
        IPage page = farmLogService.page(new Page(queryParams.getPageNum(), queryParams.getPageSize()), wrapper);

        List<FarmLogVO> list = FarmLogConverter.INSTANCE.po2voList(page.getRecords());

        return page.setRecords(list);
    }

}
