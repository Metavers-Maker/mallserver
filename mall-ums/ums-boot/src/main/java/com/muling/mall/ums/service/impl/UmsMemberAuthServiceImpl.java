package com.muling.mall.ums.service.impl;

import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.common.constant.GlobalConstants;
import com.muling.common.exception.BizException;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.mall.ums.constant.UmsConstants;
import com.muling.mall.ums.enums.AuthStatusEnum;
import com.muling.mall.ums.enums.MemberAuthStatusEnum;
import com.muling.mall.ums.event.MemberAuthSuccessEvent;
import com.muling.mall.ums.mapper.UmsMemberAuthMapper;
import com.muling.mall.ums.pojo.entity.*;
import com.muling.mall.ums.pojo.form.MemberAuthCreateForm;
import com.muling.mall.ums.pojo.vo.MemberAuthVO;
import com.muling.mall.ums.service.*;
import com.muling.mall.ums.util.ResponseAuth;
import com.muling.mall.ums.util.ResponseDTO;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class UmsMemberAuthServiceImpl extends ServiceImpl<UmsMemberAuthMapper, UmsMemberAuth> implements IUmsMemberAuthService {

    private final RabbitTemplate rabbitTemplate;

    private final IUmsMemberService memberService;

    private final RedissonClient redissonClient;

    private final IUmsMemberAuthLogService memberAuthLogService;

    private final WalletFeignClient walletFeignClient;

    private final IUmsMemberInviteService memberInviteService;

    private final IAuthService authService;

    private final IUmsAccountChainService accountChainService;

//    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean create(Long memberId, MemberAuthCreateForm memberAuthCreateForm) throws Exception {

        //记得加锁
        RLock lock = redissonClient.getLock(UmsConstants.USER_ADD_AUTH_PREFIX + memberId);
        boolean save = false;
        try {
            lock.lock();
            //查询认证状态
            UmsMember umsMember = memberService.getById(memberId);
            UmsMemberAuth memberAuth = this.baseMapper.selectOne(new LambdaQueryWrapper<UmsMemberAuth>()
                    .eq(UmsMemberAuth::getMemberId, memberId));
            if (memberAuth != null && memberAuth.getStatus() == AuthStatusEnum.PASS) {
//                throw new BizException("该用户已经通过认证");
                return true;
            }
            //开始认证
            ResponseAuth responseAuth = authService.auth(memberAuthCreateForm.getRealName(), memberAuthCreateForm.getIdCard(), umsMember.getMobile());
            boolean auth_ret = false;
            String response = JSONUtil.toJsonStr(responseAuth);
            if (responseAuth.getError_code() == 0) {
                String ret = responseAuth.getResult().getVerificationResult();
                if (StringUtils.isNotBlank(ret) && StringUtils.equals(ret, "1") == true) {
                    // 认证通过
                    auth_ret = true;
                } else {
                    // 认证失败
                    auth_ret = false;
                }
            } else {
                auth_ret = false;
            }
            if (auth_ret) {
                log.error("调用认证服务成功：{}", response);
            } else {
                log.error("调用认证服务失败：{}", response);
            }
            //
            if (memberAuth == null) {
                memberAuth = new UmsMemberAuth();
            }
            memberAuth.setMemberId(memberId);
            memberAuth.setMobile(umsMember.getMobile());
            memberAuth.setRealName(memberAuthCreateForm.getRealName());
            memberAuth.setIdCard(memberAuthCreateForm.getIdCard());
            memberAuth.setIdCardType(memberAuthCreateForm.getIdCardType());
            if (auth_ret) {
                memberAuth.setStatus(AuthStatusEnum.PASS);
                umsMember.setAuthStatus(MemberAuthStatusEnum.AUTHED.getValue());
            } else {
                memberAuth.setStatus(AuthStatusEnum.INIT);
                umsMember.setAuthStatus(MemberAuthStatusEnum.RE_AUTH.getValue());
            }
            //保存认证状态
            save = this.saveOrUpdate(memberAuth);
            memberService.updateById(umsMember);
            if (auth_ret == false) {
                throw new BizException("认证失败");
            }
            // 发送消息
            if (save) {
                //这里可以发送认证成功的消息
//                rabbitTemplate.convertAndSend(GlobalConstants.MQ_MEMBER_AUTH_QUEUE, JSONUtil.toJsonStr(memberAuth));
                if (auth_ret) {
                    //发送认证成功的消息
                    MemberAuthSuccessEvent event = new MemberAuthSuccessEvent().setMember(umsMember);
                    rabbitTemplate.convertAndSend(GlobalConstants.MQ_MEMBER_AUTH_SUCCESS_QUEUE, JSONUtil.toJsonStr(event));
                }
            }
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return save;
    }

    /**
     * 根据会员Id查询认证信息
     */
    @Override
    public MemberAuthVO queryByMemberId(Long memberId) {
        MemberAuthVO memberAuthVO = this.baseMapper.simpleOne(memberId);
        if (memberAuthVO != null) {
            memberAuthVO.setIdCard(IdcardUtil.hide(memberAuthVO.getIdCard(), 14, 18));
            memberAuthVO.setRealName(StrUtil.hide(memberAuthVO.getRealName(), 2, 3));
        }
        return memberAuthVO;
    }

    /**
     * 根据会员Id查询认证完整信息
     */
    @Override
    public MemberAuthVO queryFullByMemberId(Long memberId) {
        MemberAuthVO memberAuthVO = this.baseMapper.simpleOne(memberId);
        if (memberAuthVO != null) {
            memberAuthVO.setIdCard(memberAuthVO.getIdCard());
            memberAuthVO.setRealName(memberAuthVO.getRealName());
        }
        return memberAuthVO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean reCert(Long memberId, MemberAuthCreateForm memberAuthCreateForm) {

        RLock lock = redissonClient.getLock(UmsConstants.USER_ADD_AUTH_PREFIX + memberId);
        boolean update = false;
        try {
            lock.lock();
            UmsMemberAuth memberAuth = this.baseMapper.selectOne(new LambdaQueryWrapper<UmsMemberAuth>()
                    .eq(UmsMemberAuth::getMemberId, memberId));
            if (memberAuth.getStatus() == AuthStatusEnum.PASS) {
                throw new BizException("该用户已经通过认证");
            }
//            boolean exists = this.baseMapper.exists(new LambdaQueryWrapper<UmsMemberAuth>()
//                    .eq(UmsMemberAuth::getIdCard, memberAuthCreateForm.getIdCard())
//                    .eq(UmsMemberAuth::getStatus,AuthStatusEnum.PASS)
//            );
//            if (exists) {
//                throw new BizException("该身份证已经存在认证信息");
//            }

            UmsMember umsMember = memberService.getById(memberId);
            memberAuth.setMobile(umsMember.getMobile());
            memberAuth.setRealName(memberAuthCreateForm.getRealName());
            memberAuth.setIdCard(memberAuthCreateForm.getIdCard());
            update = this.updateById(memberAuth);

            umsMember.setAuthStatus(MemberAuthStatusEnum.AUTHING.getValue());
            memberService.updateById(umsMember);
            // 发送消息
            if (update) {
                rabbitTemplate.convertAndSend(GlobalConstants.MQ_MEMBER_AUTH_QUEUE, JSONUtil.toJsonStr(memberAuth));
            }
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return update;
    }

    @Transactional
    public void updateAuth(UmsMember umsMember, UmsMemberAuth memberAuth, String data, String response, ResponseAuth responseAuth) {
        //更新数据库
        memberService.updateById(umsMember);
        this.updateById(memberAuth);
        //更新认证日志
        memberAuthLogService.save(new UmsMemberAuthLog()
                .setMemberId(memberAuth.getMemberId())
                .setRequest(data)
                .setResponse(response)
                .setSeqNo(responseAuth.getSn())
        );
    }

    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public void authReward(MemberAuthSuccessEvent event) {
        log.error("认证服务成功 奖励：{}", event.toString());
        //发放认证奖励
        Long memberId = event.getMember().getId();
        //生成BSN账户
        accountChainService.genBsnAccountByMemberId(memberId);
        //
//        UmsMemberInvite memberInvite = memberInviteService.getInviteByMemberId(memberId);
//        if (memberInvite == null) {
//            return;
//        }
//        memberInvite.setAuthStatus(1);
//        memberInviteService.update(Wrappers.<UmsMemberInvite>lambdaUpdate()
//                .set(UmsMemberInvite::getAuthStatus, 1)
//                .eq(UmsMemberInvite::getMemberId, memberId));
//        //对邀请人发放奖励逻辑 + 记录日志
//        Long inviteMemberId = memberInvite.getInviteMemberId();
//        if (inviteMemberId > 0L) {
//        }
    }

}
