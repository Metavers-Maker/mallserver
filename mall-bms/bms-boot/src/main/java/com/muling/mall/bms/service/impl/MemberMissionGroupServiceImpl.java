package com.muling.mall.bms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.exception.BizException;
import com.muling.common.result.IResultCode;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.bms.constant.OmsConstants;
import com.muling.mall.bms.converter.MemberMissionConverter;
import com.muling.mall.bms.converter.MemberMissionGroupConverter;
import com.muling.mall.bms.mapper.MemberMissionGroupMapper;
import com.muling.mall.bms.pojo.entity.*;
import com.muling.mall.bms.pojo.query.app.MissionGroupItemPageQuery;
import com.muling.mall.bms.pojo.vo.app.MemberMissionGroupVO;
import com.muling.mall.bms.pojo.vo.app.MemberMissionVO;
import com.muling.mall.bms.service.IMemberMissionGroupService;
import com.muling.mall.bms.service.IMissionConfigService;
import com.muling.mall.bms.service.IMissionGroupConfigService;
import com.muling.mall.bms.service.IMissionLogService;
import com.muling.mall.wms.api.WalletFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class MemberMissionGroupServiceImpl extends ServiceImpl<MemberMissionGroupMapper, OmsMemberMissionGroup> implements IMemberMissionGroupService {

    private final IMissionGroupConfigService missionGroupConfigService;
    private final WalletFeignClient walletFeignClient;
//    private final MemberFeignClient memberFeignClient;
    private final IMissionLogService missionLogService;
    private final RedissonClient redissonClient;

    @Override
    public IPage<MemberMissionGroupVO> page(MissionGroupItemPageQuery queryParams) {
        //
        Long memberId = MemberUtils.getMemberId();
        LambdaQueryWrapper<OmsMemberMissionGroup> queryWrapper = new LambdaQueryWrapper<OmsMemberMissionGroup>()
                .eq(OmsMemberMissionGroup::getMemberId, memberId)
                .eq(OmsMemberMissionGroup::getName, queryParams.getName());
        //
        IPage page = this.baseMapper.selectPage(new Page(queryParams.getPageNum(), queryParams.getPageSize()), queryWrapper);
        List<MemberMissionGroupVO> list = MemberMissionGroupConverter.INSTANCE.po2voList(page.getRecords());
        return page.setRecords(list);
    }

    @Override
    @Transactional
    public MemberMissionGroupVO apply(String missionGroudName) {
        //
        Long memberId = MemberUtils.getMemberId();
        OmsMemberMissionGroup targetPO = null;
        //
        RLock lock = redissonClient.getLock(OmsConstants.MISSION_APPLYGROUP_PREFIX + memberId);
        try {
            lock.lock();
            LambdaQueryWrapper<OmsMemberMissionGroup> wrapper = Wrappers.<OmsMemberMissionGroup>lambdaQuery()
                    .eq(OmsMemberMissionGroup::getMemberId, memberId)
                    .eq(OmsMemberMissionGroup::getName, missionGroudName)
                    .orderByDesc(OmsMemberMissionGroup::getUpdated)
                    .orderByDesc(OmsMemberMissionGroup::getCreated);
            targetPO = this.getOne(wrapper);
            if (targetPO == null) {
                //查找任务包配表
                LambdaQueryWrapper<OmsMissionGroupConfig> queryWrapper = new LambdaQueryWrapper<OmsMissionGroupConfig>()
                        .eq(OmsMissionGroupConfig::getName, missionGroudName);
                OmsMissionGroupConfig missionGroupConfig = missionGroupConfigService.getOne(queryWrapper);
                if (missionGroupConfig == null) {
                    throw new RuntimeException("任务包配置不存在！"+missionGroudName);
                }
                // 创建任务包
                targetPO = new OmsMemberMissionGroup();
                targetPO.setMemberId(memberId);
                targetPO.setName(missionGroupConfig.getName());
                targetPO.setMissionGroupId(missionGroupConfig.getId());
                targetPO.setPercent(0);
                targetPO.setRewardStatus(0);
                targetPO.setStatus(0);
                boolean status = this.save(targetPO);
                if (status == false) {
                    throw new RuntimeException("任务包创建失败！");
                }
                //写日志
                OmsMissionLog itemLog = new OmsMissionLog()
                        .setMemberId(memberId)
                        .setMissionId(0l)
                        .setMissionGroupId(missionGroupConfig.getId())
                        .setLogType(0)
                        .setLogDsp("任务包apply");
                missionLogService.save(itemLog);
            }
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
        MemberMissionGroupVO targetVO = MemberMissionGroupConverter.INSTANCE.po2vo(targetPO);
        return targetVO;
    }

    @Override
    @Transactional
    public boolean claim(Long id) {
        Long memberId = MemberUtils.getMemberId();
        OmsMemberMissionGroup memberMissionGroup = this.getById(id);
        try {
            if (memberMissionGroup == null) {
                throw new RuntimeException("用户没有相应的任务包");
            }
            //
        } catch (Exception e) {
            throw e;
        }
        return true;
    }
}
