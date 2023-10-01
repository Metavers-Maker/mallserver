package com.muling.mall.farm.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.enums.BusinessTypeEnum;
import com.muling.common.redis.utils.BusinessNoGenerator;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.farm.constant.FarmConstants;
import com.muling.mall.farm.converter.FarmAdConverter;
import com.muling.mall.farm.mapper.FarmAdMapper;
import com.muling.mall.farm.pojo.dto.FarmAdItemDTO;
import com.muling.mall.farm.pojo.entity.*;
import com.muling.mall.farm.pojo.vo.app.FarmAdVO;
import com.muling.mall.farm.service.*;
import com.muling.mall.ums.api.MemberInviteFeignClient;
import com.muling.mall.ums.pojo.dto.AdFarmDispatchDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class FarmAdServiceImpl extends ServiceImpl<FarmAdMapper, FarmAd> implements IFarmAdService {

    private final MemberInviteFeignClient memberInviteFeignClient;

    private final RedissonClient redissonClient;

    private final IFarmAdItemService farmAdItemService;

    private final BusinessNoGenerator businessNoGenerator;

    private final IFarmConfigService farmConfigService;

    //领取一个任务
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FarmAdVO openAd() {
        Long memberId = MemberUtils.getMemberId();
        RLock lock = redissonClient.getLock(FarmConstants.FARM_AD_OPEN_PREFIX + memberId);
        try {
            lock.lock();
            //获得农场包配置
            FarmConfig farmConfig = farmConfigService.getById(1);
            Assert.isTrue(farmConfig!=null,"未发现工作包配置");
            //
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime started = now.withHour(0).withMinute(0).withSecond(0);
            LocalDateTime ended = now.withHour(23).withMinute(59).withSecond(59);
            QueryWrapper<FarmAd> queryWrapper = new QueryWrapper<FarmAd>()
                    .eq("member_id", memberId)
                    .ge(started != null, "updated", started)
                    .le(ended != null, "updated", ended);
            FarmAd farmAd = this.baseMapper.selectOne(queryWrapper);
            if (farmAd == null) {
                //创建今天的任务
                farmAd = new FarmAd();
                Long adsn = businessNoGenerator.generateLong(BusinessTypeEnum.AD);
                farmAd.setMemberId(memberId);
                farmAd.setAdSn(adsn);
                farmAd.setAdType(1);    //Farm类型（8个100任务值）
                farmAd.setStatus(0);
                farmAd.setRewardCoinType(5);
                farmAd.setRewardCoinValue(farmConfig.getAdReward());
                farmAd.setStep(0);
                farmAd.setEnsure(LocalDateTime.now());
                boolean f = this.save(farmAd);
                if (f) {
                    log.info("Farm创建Ad任务:{}", JSONUtil.toJsonStr(farmAd));
                    FarmAdVO farmAdVO = FarmAdConverter.INSTANCE.po2vo(farmAd);
                    return farmAdVO;
                }
            }
            Assert.isTrue(farmAd.getStep().intValue() < farmConfig.getMaxNum(), "今日任务已完成");
            log.info("Farm领取Ad任务:{}", JSONUtil.toJsonStr(farmAd));
            FarmAdVO farmAdVO = FarmAdConverter.INSTANCE.po2vo(farmAd);
            return farmAdVO;
        } catch (Exception e) {
            log.error("", e);
            throw e;
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FarmAdVO rewardAd() {
        Long memberId = MemberUtils.getMemberId();
        RLock lock = redissonClient.getLock(FarmConstants.FARM_AD_REWARD_PREFIX + memberId);
        try {
            lock.lock();
            //获得农场包配置
            FarmConfig farmConfig = farmConfigService.getById(1);
            Assert.isTrue(farmConfig!=null,"未发现工作包配置");
            //
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime started = now.withHour(0).withMinute(0).withSecond(0);
            LocalDateTime ended = now.withHour(23).withMinute(59).withSecond(59);
            QueryWrapper<FarmAd> queryWrapper = new QueryWrapper<FarmAd>()
                    .eq("member_id", memberId)
                    .ge(started != null, "updated", started)
                    .le(ended != null, "updated", ended);
            FarmAd farmAd = this.baseMapper.selectOne(queryWrapper);
            Assert.isTrue(farmAd != null, "任务不存在，不能领取奖励");
            Assert.isTrue(farmAd.getStep().compareTo(farmConfig.getMaxNum()) >= 0, "任务未完成");
            Assert.isTrue(farmAd.getStatus().intValue() == 0, "已经领取奖励，不能重复领取");
            log.info("Farm领取Ad任务奖励:{}", JSONUtil.toJsonStr(farmAd));
            //
            farmAd.setStatus(1);
            boolean f = this.updateById(farmAd);
            if (f) {
                //发放奖励
                AdFarmDispatchDTO adFarmDispatchDTO = new AdFarmDispatchDTO();
                adFarmDispatchDTO.setMemberId(memberId);
                adFarmDispatchDTO.setCoinType(farmAd.getRewardCoinType());
                adFarmDispatchDTO.setCoinValue(farmConfig.getAdReward());
                adFarmDispatchDTO.setDirectValue(farmConfig.getAdUpReward());
                adFarmDispatchDTO.setAdLevel1Value(farmConfig.getAdL1Reward());
                adFarmDispatchDTO.setAdLevel2Value(farmConfig.getAdL2Reward());
                adFarmDispatchDTO.setAdLevel3Value(farmConfig.getAdL3Reward());
                adFarmDispatchDTO.setAdLevel4Value(farmConfig.getAdL4Reward());
                memberInviteFeignClient.adFarmDispatch(adFarmDispatchDTO);
            }
            //
            FarmAdVO farmAdVO = FarmAdConverter.INSTANCE.po2vo(farmAd);
            return farmAdVO;
        } catch (Exception e) {
            log.error("", e);
            throw e;
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer stepAd() {
        //获取今天任务的进度
        Long memberId = MemberUtils.getMemberId();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime started = now.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime ended = now.withHour(23).withMinute(59).withSecond(59);
        QueryWrapper<FarmAd> queryWrapper = new QueryWrapper<FarmAd>()
                .eq("member_id", memberId)
                .ge(started != null, "updated", started)
                .le(ended != null, "updated", ended);
        FarmAd farmAd = this.baseMapper.selectOne(queryWrapper);
        Assert.isTrue(farmAd != null, "任务不存在，不能领取奖励");
        return farmAd.getStep();
    }

    public boolean adCallback(FarmAdItemDTO farmAdItemDTO) {
        //执行逻辑之前先判断transaction_id是否已经执行过了，防止重复发放奖励
        boolean b = farmAdItemService.saveDTO(farmAdItemDTO);
        if (b) {
            RLock lock = redissonClient.getLock(FarmConstants.FARM_AD_STEP_PREFIX + farmAdItemDTO.getAdSn());
            try {
                lock.lock();
                //保存成功，修改Step
                QueryWrapper<FarmAd> queryWrapper = new QueryWrapper<FarmAd>()
                        .eq("ad_sn", farmAdItemDTO.getAdSn());
                FarmAd farmAd = this.baseMapper.selectOne(queryWrapper);
                if (farmAd != null) {
                    log.info("进度:ad_sn{},step:{}", farmAdItemDTO.getAdSn(), farmAd.getStep().intValue() + 1);
                    farmAd.setStep(farmAd.getStep().intValue() + 1);
                    return this.updateById(farmAd);
                }
            } catch (Exception e) {
                log.error("", e);
                throw e;
            } finally {
                //释放锁
                if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
        return b;
    }

    public boolean stepAdGo(Long adSn) {

        RLock lock = redissonClient.getLock(FarmConstants.FARM_AD_STEP_PREFIX + adSn);
        try {
            lock.lock();
            //保存成功，修改Step
            QueryWrapper<FarmAd> queryWrapper = new QueryWrapper<FarmAd>()
                    .eq("ad_sn", adSn);
            FarmAd farmAd = this.baseMapper.selectOne(queryWrapper);
            if (farmAd != null) {
                farmAd.setStep(farmAd.getStep().intValue() + 1);
                return this.updateById(farmAd);
            }
        } catch (Exception e) {
            log.error("", e);
            throw e;
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return false;
    }

    //判断是否完成
    public boolean isComplete(Integer maxNum) {
        Long memberId = MemberUtils.getMemberId();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime started = now.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime ended = now.withHour(23).withMinute(59).withSecond(59);
        QueryWrapper<FarmAd> queryWrapper = new QueryWrapper<FarmAd>()
                .eq("member_id", memberId)
                .ge(started != null, "updated", started)
                .le(ended != null, "updated", ended);
        FarmAd farmAd = this.baseMapper.selectOne(queryWrapper);
        if (farmAd != null) {
            return farmAd.getStep().compareTo(maxNum)>=0;
        }
        return false;
    }
    //
}
