package com.muling.mall.ums.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.muling.common.constant.RedisConstants;
import com.muling.common.exception.BizException;
import com.muling.common.redis.utils.RedisUtils;
import com.muling.common.result.PageResult;
import com.muling.mall.ums.converter.MemberConverter;
import com.muling.mall.ums.enums.FollowStatusEnum;
import com.muling.mall.ums.mapper.UmsRelationMapper;
import com.muling.mall.ums.pojo.dto.RelationDTO;
import com.muling.mall.ums.pojo.entity.UmsMember;
import com.muling.mall.ums.pojo.entity.UmsRelation;
import com.muling.mall.ums.pojo.vo.MemberCoinRank;
import com.muling.mall.ums.service.IUmsMemberService;
import com.muling.mall.ums.service.IUmsRelationService;
import com.muling.mall.wms.api.WalletFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class UmsRelationServiceImpl extends ServiceImpl<UmsRelationMapper, UmsRelation> implements IUmsRelationService {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private RedisUtils redisUtils;

    private final IUmsMemberService memberService;

    private final WalletFeignClient walletFeignClient;

    @Override
    public IPage<RelationDTO> follows(Page<RelationDTO> page, Long memberId) {

        MPJLambdaWrapper<RelationDTO> wrapper = new MPJLambdaWrapper<RelationDTO>()
                .select(UmsRelation::getId, UmsRelation::getMemberId, UmsRelation::getFollowId, UmsRelation::getStatus, UmsRelation::getUpdated)
                .selectAs(UmsMember::getNickName, RelationDTO::getFollowName)
                .select(UmsMember::getAvatarUrl)
                .leftJoin(UmsMember.class, UmsMember::getId, UmsRelation::getFollowId)
                .eq(UmsRelation::getMemberId, memberId)
                .ne(UmsRelation::getStatus, FollowStatusEnum.CANCEL);
        IPage p = this.baseMapper.selectJoinPage(page, RelationDTO.class, wrapper);
        return p;
    }

    @Override
    public IPage<RelationDTO> fans(Page<RelationDTO> page, Long memberId) {

        MPJLambdaWrapper<RelationDTO> wrapper = new MPJLambdaWrapper<RelationDTO>()
                .select(UmsRelation::getId, UmsRelation::getMemberId, UmsRelation::getFollowId, UmsRelation::getStatus, UmsRelation::getUpdated)
                .selectAs(UmsMember::getNickName, RelationDTO::getFollowName)
                .select(UmsMember::getAvatarUrl)
                .leftJoin(UmsMember.class, UmsMember::getId, UmsRelation::getMemberId)
                .eq(UmsRelation::getFollowId, memberId)
                .ne(UmsRelation::getStatus, FollowStatusEnum.CANCEL);
        IPage p = this.baseMapper.selectJoinPage(page, RelationDTO.class, wrapper);
        return p;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean follow(Long memberId, Long followId) {

        UmsRelation memberFollow = getOne(new LambdaQueryWrapper<UmsRelation>()
                .eq(UmsRelation::getMemberId, memberId)
                .eq(UmsRelation::getFollowId, followId));

        UmsRelation followMember = getOne(new LambdaQueryWrapper<UmsRelation>()
                .eq(UmsRelation::getMemberId, followId)
                .eq(UmsRelation::getFollowId, memberId));
        if (memberFollow == null) {
            memberFollow = new UmsRelation()
                    .setMemberId(memberId)
                    .setFollowId(followId);
        }
        if (followMember == null || followMember.getStatus() == FollowStatusEnum.CANCEL) {
            memberFollow.setStatus(FollowStatusEnum.FOLLOW);
        }
        if (followMember != null && followMember.getStatus() == FollowStatusEnum.FOLLOW) {
            memberFollow.setStatus(FollowStatusEnum.BOTH);
            followMember.setStatus(FollowStatusEnum.BOTH);
            saveOrUpdate(followMember);
        }
        redisUtils.incr(RedisConstants.FOLLOWER + memberId, 1L);
        redisUtils.incr(RedisConstants.FOLLOWING + followId, 1L);

        return saveOrUpdate(memberFollow);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean unFollow(Long memberId, Long followId) {

        UmsRelation memberFollow = getOne(new LambdaQueryWrapper<UmsRelation>()
                .eq(UmsRelation::getMemberId, memberId)
                .eq(UmsRelation::getFollowId, followId));

        UmsRelation followMember = getOne(new LambdaQueryWrapper<UmsRelation>()
                .eq(UmsRelation::getMemberId, followId)
                .eq(UmsRelation::getFollowId, memberId));

        if (memberFollow == null) {
            throw new BizException("");
        }
        memberFollow.setStatus(FollowStatusEnum.CANCEL);

        if (followMember != null && followMember.getStatus() == FollowStatusEnum.BOTH) {
            followMember.setStatus(FollowStatusEnum.FOLLOW);
            saveOrUpdate(followMember);
        }
        redisUtils.decr(RedisConstants.FOLLOWER + memberId, 1L);
        redisUtils.decr(RedisConstants.FOLLOWING + followId, 1L);

        return saveOrUpdate(memberFollow);
    }

    @Override
    public int getFollowCount(long memberId) {
        Object count = redisUtils.get(RedisConstants.FOLLOWER + memberId);
        if (count != null) {
            return Long.valueOf(String.valueOf(count)).intValue();
        } else {
            return 0;
        }
    }

    @Override
    public int getFansCount(long memberId) {
        Object count = redisUtils.get(RedisConstants.FOLLOWING + memberId);
        if (count != null) {
            return Long.valueOf(String.valueOf(count)).intValue();
        } else {
            return 0;
        }
    }

    @Override
    public List<MemberCoinRank> rankByCoin(Long pageNum, Long pageSize, Integer coinType) {
        //
        List<MemberCoinRank> memberCoinRanks = new ArrayList<>();
        //
        PageResult pageResult = walletFeignClient.page(pageNum, pageSize, coinType);
        List<LinkedHashMap<String, Object>> walletList = pageResult.getData().getList();
        walletList.forEach(wmsItem -> {
            Long memberId = Long.valueOf(wmsItem.get("memberId").toString());
            BigDecimal balance = new BigDecimal(wmsItem.get("balance").toString());
            UmsMember member = memberService.getById(memberId);
            if (member != null) {
                MemberCoinRank memberCoinRank = MemberConverter.INSTANCE.po2rank(member);
                memberCoinRank.setBalance(balance);
                memberCoinRanks.add(memberCoinRank);
            }
        });
        return memberCoinRanks;
    }
}
