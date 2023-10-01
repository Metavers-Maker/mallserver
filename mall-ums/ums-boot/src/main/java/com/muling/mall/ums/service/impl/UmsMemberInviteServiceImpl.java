package com.muling.mall.ums.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Joiner;
import com.muling.common.base.BasePageQuery;
import com.muling.common.constant.GlobalConstants;
import com.muling.common.exception.BizException;
import com.muling.common.result.ResultCode;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.ums.constant.UmsConstants;
import com.muling.mall.ums.converter.MemberInviteConverter;
import com.muling.mall.ums.enums.MemberAuthStatusEnum;
import com.muling.mall.ums.event.MemberRegisterEvent;
import com.muling.mall.ums.mapper.UmsMemberInviteMapper;
import com.muling.mall.ums.pojo.dto.AdFarmDispatchDTO;
import com.muling.mall.ums.pojo.dto.AdMissionDispatchDTO;
import com.muling.mall.ums.pojo.dto.MemberRegisterDTO;
import com.muling.mall.ums.pojo.entity.UmsMemberInvite;
import com.muling.mall.ums.pojo.form.MemberInviteForm;
import com.muling.mall.ums.pojo.vo.MemberInviteVO;
import com.muling.mall.ums.service.IUmsMemberInviteService;
import com.muling.mall.wms.api.WalletFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class UmsMemberInviteServiceImpl extends ServiceImpl<UmsMemberInviteMapper, UmsMemberInvite> implements IUmsMemberInviteService {

    private final RedissonClient redissonClient;

    private final RabbitTemplate rabbitTemplate;

    private final StringRedisTemplate stringRedisTemplate;

    private final WalletFeignClient walletFeignClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addInvite(MemberRegisterEvent event) {
        MemberRegisterDTO inviteMember = event.getInviteMember();
        if (inviteMember == null) {
            return;
        }
        MemberRegisterDTO member = event.getMember();
        UmsMemberInvite memberInvite = new UmsMemberInvite();
        memberInvite.setMemberId(member.getId());
        memberInvite.setInviteMemberId(inviteMember.getId());
        memberInvite.setInviteCode(inviteMember.getInviteCode());
        memberInvite.setAuthStatus(MemberAuthStatusEnum.UN_AUTH.getValue());
        this.save(memberInvite);
    }

    @Override
    public UmsMemberInvite getRefereeByMemberId(Long memberId) {
        UmsMemberInvite memberInvite = this.baseMapper.getByMemberId(memberId);
        Long inviteMemberId = memberInvite.getInviteMemberId();
        if (inviteMemberId != null && inviteMemberId > 0) {
            return this.baseMapper.getByMemberId(inviteMemberId);
        }
        return null;
    }

    @Override
    public UmsMemberInvite getInviteByMemberId(Long memberId) {
        UmsMemberInvite memberInvite = this.baseMapper.getByMemberId(memberId);
        return memberInvite;
    }

    @Override
    public IPage<MemberInviteVO> listInvitesByInviteMemberIdWithPage(BasePageQuery query) {
        Long memberId = MemberUtils.getMemberId();
        LambdaQueryWrapper<UmsMemberInvite> wrapper = Wrappers.<UmsMemberInvite>lambdaQuery();
        wrapper.eq(UmsMemberInvite::getInviteMemberId, memberId);
        wrapper.orderByDesc(UmsMemberInvite::getCreated);
        Page<UmsMemberInvite> page = this.page(new Page<UmsMemberInvite>(query.getPageNum(), query.getPageSize()), wrapper);
        Page<MemberInviteVO> result = MemberInviteConverter.INSTANCE.entity2PageVO(page);
        return result;
    }

    @Override
    public boolean starDispatch(Integer star, BigDecimal fee) {
        return true;
    }

    public boolean adMissionDispatch(AdMissionDispatchDTO adMissionDispatchDTO) {
        return true;
    }

    public boolean adFarmDispatch(AdFarmDispatchDTO adFarmDispatchDTO) {
        return true;
    }

    //农场分红
    public boolean gameFarmDispatch(AdFarmDispatchDTO adFarmDispatchDTO) {
        return true;
    }

    @Override
    public boolean setInviteCode(Long memberId, String inviteCode) {
        RLock lock = redissonClient.getLock(UmsConstants.USER_INVITE_CODE_PREFIX + inviteCode);
        boolean status = false;
        try {
            lock.lock();
//            //用户已设置邀请码
//            UmsMemberInvite memberInvite = baseMapper.selectOne(Wrappers.<UmsMemberInvite>lambdaQuery().eq(UmsMemberInvite::getMemberId, memberId));
//            if (StrUtil.isNotBlank(memberInvite.getInviteCode())) {
//                throw new BizException(ResultCode.USER_INVITE_CODE_EXIST);
//            }
//            //邀请码已存在
//            boolean exists = baseMapper.exists(new LambdaQueryWrapper<UmsMemberInvite>().eq(UmsMemberInvite::getInviteCode, inviteCode));
//            if (exists) {
//                throw new BizException(ResultCode.USER_INVITE_CODE_EXIST);
//            }
//            //更新邀请码
//            LambdaUpdateWrapper<UmsMemberInvite> updateInviteWrapper = Wrappers.<UmsMemberInvite>lambdaUpdate()
//                    .set(UmsMemberInvite::getInviteCode, inviteCode)
//                    .eq(UmsMemberInvite::getMemberId, memberId);
//            status = update(updateInviteWrapper);
        } catch (Exception e) {
            log.error("", e);
            throw e;
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return status;
    }

    @Override
    public UmsMemberInvite getByInviteCode(String inviteCode) {
        return baseMapper.selectOne(Wrappers.<UmsMemberInvite>lambdaQuery().eq(UmsMemberInvite::getInviteCode, inviteCode));
    }


    @Override
    public boolean update(Long id, MemberInviteForm form) {
        UmsMemberInvite memberInvite = getById(id);
        if (memberInvite == null) {
            throw new BizException(ResultCode.DATA_NOT_EXIST);
        }
        MemberInviteConverter.INSTANCE.updatePo(form, memberInvite);
        return updateById(memberInvite);
    }

    @Override
    public boolean feng(Long memberId, Integer status) {
        UmsMemberInvite umsMemberInvite = this.getOne(Wrappers.<UmsMemberInvite>lambdaUpdate()
                .eq(UmsMemberInvite::getMemberId, memberId));
        if (umsMemberInvite != null) {
            JSONObject extJson = null;
            String ext = umsMemberInvite.getExt();
            if (ext != null) {
                extJson = JSONUtil.parseObj(ext);
            } else {
                extJson = new JSONObject();
            }
            extJson.set("feng", status);
            return this.update(Wrappers.<UmsMemberInvite>lambdaUpdate()
                    .set(UmsMemberInvite::getExt, extJson.toString())
                    .eq(UmsMemberInvite::getMemberId, memberId));
        }
        return true;
    }


    public static void main(String[] args) {
        System.out.println(Joiner.on(",").join(StrUtil.blankToDefault(null, ""), 1));
    }
}
