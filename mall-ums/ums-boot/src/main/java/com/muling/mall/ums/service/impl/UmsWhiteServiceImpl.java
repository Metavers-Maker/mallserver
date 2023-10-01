package com.muling.mall.ums.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.muling.common.constant.GlobalConstants;
import com.muling.common.constant.RedisConstants;
import com.muling.common.exception.BizException;
import com.muling.common.redis.utils.RedisUtils;
import com.muling.common.result.ResultCode;
import com.muling.mall.ums.constant.UmsConstants;
import com.muling.mall.ums.enums.AuthStatusEnum;
import com.muling.mall.ums.enums.FollowStatusEnum;
import com.muling.mall.ums.enums.MemberAuthStatusEnum;
import com.muling.mall.ums.event.MemberAuthSuccessEvent;
import com.muling.mall.ums.mapper.UmsRelationMapper;
import com.muling.mall.ums.mapper.UmsWhiteMapper;
import com.muling.mall.ums.pojo.dto.RelationDTO;
import com.muling.mall.ums.pojo.entity.UmsMember;
import com.muling.mall.ums.pojo.entity.UmsMemberAuth;
import com.muling.mall.ums.pojo.entity.UmsRelation;
import com.muling.mall.ums.pojo.entity.UmsWhite;
import com.muling.mall.ums.pojo.form.MemberWhiteForm;
import com.muling.mall.ums.service.IUmsMemberService;
import com.muling.mall.ums.service.IUmsRelationService;
import com.muling.mall.ums.service.IUmsWhiteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service
@Slf4j
@RequiredArgsConstructor
public class UmsWhiteServiceImpl extends ServiceImpl<UmsWhiteMapper, UmsWhite> implements IUmsWhiteService {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private RedisUtils redisUtils;

    private final RedissonClient redissonClient;

    private final IUmsMemberService memberService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean create(Long memberId,MemberWhiteForm whiteForm) {
        //记得加锁
        RLock lock = redissonClient.getLock(UmsConstants.USER_ADD_AUTH_PREFIX + memberId);
        boolean save = false;
        try {
            lock.lock();
            boolean exists = this.baseMapper.exists(new LambdaQueryWrapper<UmsWhite>()
                    .eq(UmsWhite::getMemberId, memberId));
            if (exists) {
                throw new BizException(ResultCode.REQUEST_INVALID, "已经存在认证信息");
            }
            UmsMember umsMember = memberService.getById(memberId);
            UmsWhite white = new UmsWhite();
            white.setMemberId(memberId);
            white.setMobile(umsMember.getMobile());
            white.setLevel(whiteForm.getLevel());
            save = this.save(white);
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return save;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateLevel(Integer level,String mobile) {
        UmsWhite umsWhite = getOne(new LambdaQueryWrapper<UmsWhite>()
                .eq(UmsWhite::getMobile, mobile));
        if (umsWhite == null) {
            umsWhite = new UmsWhite();
            umsWhite.setMobile(mobile);
            umsWhite.setLevel(level);
            return this.save(umsWhite);
        }
        umsWhite.setLevel(level);
        return this.updateById(umsWhite);
    };

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer whiteLevel(String mobile) {
        UmsWhite white = getOne(new LambdaQueryWrapper<UmsWhite>()
                .eq(UmsWhite::getMobile, mobile));
        if (white == null) {
            return 0;
        }
        return white.getLevel();
    }

}
