package com.muling.mall.bms.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.muling.common.result.Result;
import com.muling.mall.bms.enums.ItemLogTypeEnum;
import com.muling.mall.bms.pojo.entity.OmsItemLog;
import com.muling.mall.bms.pojo.entity.OmsMemberMission;
import com.muling.mall.bms.pojo.entity.OmsMissionConfig;
import com.muling.mall.bms.pojo.entity.OmsMissionLog;
import com.muling.mall.bms.service.IMemberMissionService;
import com.muling.mall.bms.service.IMissionConfigService;
//import org.redisson.api.RedissonClient;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.exception.BizException;
import com.muling.common.result.IResultCode;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.bms.constant.OmsConstants;
import com.muling.mall.bms.converter.MemberMissionConverter;
import com.muling.mall.bms.mapper.MemberMissionMapper;
import com.muling.mall.bms.pojo.form.app.MemberMissionForm;
import com.muling.mall.bms.pojo.vo.app.MemberMissionVO;
import com.muling.mall.bms.service.IMissionLogService;
import com.muling.mall.ums.api.MemberFeignClient;
import com.muling.mall.ums.api.MemberInviteFeignClient;
import com.muling.mall.ums.pojo.dto.AdMissionDispatchDTO;
import com.muling.mall.ums.pojo.dto.MemberDTO;
import com.muling.mall.ums.pojo.dto.MemberInviteDTO;
import com.muling.mall.wms.api.WalletFeignClient;
import com.muling.mall.wms.enums.WalletOpTypeEnum;
import com.muling.mall.wms.pojo.dto.WalletDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class MemberMissionServiceImpl extends ServiceImpl<MemberMissionMapper, OmsMemberMission> implements IMemberMissionService {
//    private final IMemberItemService memberItemService;
    private final WalletFeignClient walletFeignClient;
    private final MemberFeignClient memberFeignClient;
    private final MemberInviteFeignClient memberInviteFeignClient;
    private final IMissionLogService missionLogService;
    private final RedissonClient redissonClient;

    private final IMissionConfigService missionConfigService;

    @Override
    @Transactional
    public MemberMissionVO apply(Long id) {
        OmsMemberMission memberMission = null;
        Long memberId = MemberUtils.getMemberId();
        RLock lock = redissonClient.getLock(OmsConstants.MISSION_APPLY_PREFIX + memberId);
        try {
            lock.lock();
            //查找目标用户
            Result<MemberDTO> memberDTOResult = memberFeignClient.getMemberById(memberId);
            if(memberDTOResult.getData()!=null) {
                Assert.isTrue(memberDTOResult.getData().getStatus().intValue() == 1,"账号被封");
            }
            // 查找身上是否有相同配置ID的任务
            LambdaQueryWrapper<OmsMemberMission> queryWrapper = new LambdaQueryWrapper<OmsMemberMission>()
                    .eq(OmsMemberMission::getMemberId, memberId)
                    .eq(OmsMemberMission::getMissionConfigId, id)
                    .orderByDesc(OmsMemberMission::getUpdated);
            Page<OmsMemberMission> page = this.baseMapper.selectPage(new Page(1, 10), queryWrapper);
            //
            Assert.isTrue(page.getTotal() == 0, "不要重复创建相同任务！");
            // 获取任务配置
            OmsMissionConfig missionConfig = missionConfigService.getById(id);
            Assert.isTrue(missionConfig != null, "配表中查询不到相应任务！");
            Assert.isTrue(missionConfig.getGroupId() != null, "任务没有配置任务包！"+id);
            Assert.isTrue(missionConfig.getReNum() > 0, "剩余任务不足！");
            //
            if( missionConfig.getCostCoinValue().compareTo(BigDecimal.ZERO) == 1) {
                //需要扣除相关的代币
                Result<BigDecimal> coinValue = walletFeignClient.getCoinValueByMemberIdAndCoinType(memberId,missionConfig.getCostCoinType());
                Assert.isTrue(coinValue.getData().compareTo(missionConfig.getCostCoinValue())>0, "积分不足！");
                //更新用户代币
                WalletDTO walletDTO = new WalletDTO();
                walletDTO.setMemberId(memberId);
                walletDTO.setOpType(WalletOpTypeEnum.MISSION_COMPLETE_COST.getValue());
                walletDTO.setCoinType(missionConfig.getCostCoinType());
                walletDTO.setBalance(missionConfig.getCostCoinValue().multiply(new BigDecimal(-1.0)));
                walletDTO.setRemark("领取任务消耗");
                //walletDTO.setFee();
                walletFeignClient.updateBalance(walletDTO);
            }

            // 创建任务
            memberMission = new OmsMemberMission();
            memberMission.setMissionConfigId(missionConfig.getId());
            memberMission.setMissionGroupId(missionConfig.getGroupId());
            memberMission.setMemberId(memberId);
            memberMission.setName(missionConfig.getName());
            memberMission.setStatus(0);
            memberMission.setReward(0);
            memberMission.setContent(null);
            this.save(memberMission);

            //写日志
            OmsMissionLog itemLog = new OmsMissionLog()
                    .setMemberId(memberId)
                    .setMissionId(missionConfig.getId())
                    .setMissionGroupId(missionConfig.getGroupId())
                    .setLogType(0)
                    .setLogDsp("任务apply");
            missionLogService.save(itemLog);
            // 修改配表数据
            // 总量 -1
            Integer reNum = missionConfig.getReNum();
            missionConfig.setReNum(reNum - 1);
            // 释放量 +1
            Integer mintNum = missionConfig.getMintNum();
            missionConfig.setMintNum(mintNum + 1);
            //更新配表
            missionConfigService.updateById(missionConfig);
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
        //
        return MemberMissionConverter.INSTANCE.po2vo(memberMission);
    }

    @Override
    @Transactional
    public boolean submit(Long id, MemberMissionForm form) {
        //
        System.out.println("submit id");
        //修改任务数据和状态
        Long memberId = MemberUtils.getMemberId();
        LambdaQueryWrapper<OmsMemberMission> queryWrapper = new LambdaQueryWrapper<OmsMemberMission>()
                .eq(OmsMemberMission::getMemberId, memberId)
                .eq(OmsMemberMission::getId, id)
                .orderByDesc(OmsMemberMission::getUpdated);
        OmsMemberMission memberMission = this.baseMapper.selectOne(queryWrapper);
        Assert.isTrue(memberMission != null, "任务不存在！");
        Assert.isTrue(memberMission.getStatus() != 1, "任务审核中，不要重复提交");
        Assert.isTrue(memberMission.getStatus() != 2, "任务已通过！不允许重复提交");
        //设置状态，提交任务
        memberMission.setStatus(1);
        memberMission.setContent(form.getContent());
        this.baseMapper.updateById(memberMission);
        //写日志
        OmsMissionLog itemLog = new OmsMissionLog()
                .setMemberId(memberId)
                .setMissionId(id)
                .setMissionGroupId(0L)
                .setLogType(1)
                .setLogDsp("任务submit");
        missionLogService.save(itemLog);
        return true;
    }

    @Override
    @Transactional
    public boolean check(Long id,Integer state) {
        /*
        *state = ?
        * 0: 未开始
        * 1: 已提交
        * 2：通过
        * 3：失败
        * */
        boolean status = false;
        RLock lock = redissonClient.getLock(OmsConstants.MISSION_CHECK_PREFIX + id);
        try {
            lock.lock();
            //更新用户任务状态
            OmsMemberMission memberMission = this.getById(id);
            Assert.isTrue(memberMission != null, "任务不存在！"+id);
            memberMission.setStatus(state);
            if (state == 2 && memberMission.getReward() == 0) {
                //状态是完成 && 还没发放奖励
                //增加用户的积分
                OmsMissionConfig missionConfig = missionConfigService.getById(memberMission.getMissionConfigId());
                if (missionConfig != null) {
                    //标记方法完奖励
                    memberMission.setReward(1);
                    //发放奖励
                    AdMissionDispatchDTO adMissionDispatchDTO = new AdMissionDispatchDTO();
                    adMissionDispatchDTO.setMemberId(memberMission.getMemberId());
                    adMissionDispatchDTO.setCoinType(missionConfig.getClaimCoinType());
                    adMissionDispatchDTO.setCoinValue(missionConfig.getClaimCoinValue());
                    adMissionDispatchDTO.setDirectValue(missionConfig.getClaimCoinValue().multiply(new BigDecimal(0.5)));
                    adMissionDispatchDTO.setAdLevel1Value(missionConfig.getClaimCoinValue().multiply(new BigDecimal(0.125)));
                    adMissionDispatchDTO.setAdLevel2Value(missionConfig.getClaimCoinValue().multiply(new BigDecimal(0.125)));
                    adMissionDispatchDTO.setAdLevel3Value(missionConfig.getClaimCoinValue().multiply(new BigDecimal(0.125)));
                    adMissionDispatchDTO.setAdLevel4Value(missionConfig.getClaimCoinValue().multiply(new BigDecimal(0.125)));
                    memberInviteFeignClient.adMissionDispatch(adMissionDispatchDTO);
                }
            }
            //更新任务数据
            status = this.updateById(memberMission);
        }
        catch (BizException e) {
            IResultCode resultCode = e.getResultCode();
            log.error("{}.{}", resultCode.getCode(), resultCode.getMsg());
            throw e;
        } catch (Exception e) {
            log.error("", e);
            throw e;
        }
        finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return status;
    }

}
