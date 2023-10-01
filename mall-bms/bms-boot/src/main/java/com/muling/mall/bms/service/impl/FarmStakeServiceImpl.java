package com.muling.mall.bms.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.mall.bms.enums.ItemFreezeTypeEnum;
import com.muling.mall.bms.enums.ItemLogTypeEnum;
import com.muling.mall.bms.mapper.FarmStakeMapper;
import com.muling.mall.bms.pojo.dto.StakeDTO;
import com.muling.mall.bms.pojo.entity.OmsFarmPool;
import com.muling.mall.bms.pojo.entity.OmsFarmStake;
import com.muling.mall.bms.pojo.entity.OmsFarmStakeItem;
import com.muling.mall.bms.pojo.entity.OmsMemberItem;
import com.muling.mall.bms.service.IFarmStakeItemService;
import com.muling.mall.bms.service.IFarmStakeService;
import com.muling.mall.bms.service.IMemberItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class FarmStakeServiceImpl extends ServiceImpl<FarmStakeMapper, OmsFarmStake> implements IFarmStakeService {

    private final IFarmStakeItemService stakeItemService;
    private final IMemberItemService memberItemService;

    @Override
    @Transactional
    public void stake(OmsMemberItem item, StakeDTO stakeDTO, OmsFarmPool.Rule rule) {

        //冻结物品
        item.setFreezeType(ItemFreezeTypeEnum.FARM);
        memberItemService.freeze(item, ItemLogTypeEnum.STAKE);
        Long memberId = item.getMemberId();

        //创建质押记录
        OmsFarmStake stakeMember = getOne(Wrappers.<OmsFarmStake>lambdaQuery()
                .eq(OmsFarmStake::getMemberId, memberId)
                .eq(OmsFarmStake::getPoolId, stakeDTO.getPoolId())
        );
        if (stakeMember != null) {
            updateById(stakeMember
                    .setAllocPoint(stakeMember.getAllocPoint() + rule.getAllocPoint())
                    .setTotal(stakeMember.getTotal() + 1)
            );
        } else {
            stakeMember = new OmsFarmStake();
            save(stakeMember
                    .setMemberId(memberId)
                    .setPoolId(stakeDTO.getPoolId())
                    .setAllocPoint(rule.getAllocPoint())
                    .setSpuId(item.getSpuId())
                    .setTotal(1)
            );
        }

        //锁仓物品
        OmsFarmStakeItem stakeItem = new OmsFarmStakeItem()
                .setMemberId(memberId)
                .setPoolId(stakeDTO.getPoolId())
                .setStakeMemberId(stakeMember.getId())
                .setSpuId(item.getSpuId())
                .setItemId(item.getId())
                .setItemNo(item.getItemNo())
                .setItemName(item.getName())
                .setPicUrl(item.getPicUrl())
                .setDays(stakeDTO.getDays())
                .setAllocPoint(rule.getAllocPoint())
                .setCurrentDays(0);
        stakeItemService.stakeItem(stakeItem);

    }

    @Override
    @Transactional
    public OmsFarmStakeItem withdraw(OmsMemberItem item, OmsFarmStakeItem stakeMemberItem) {
        //冻结物品
        memberItemService.unFreeze(item, ItemLogTypeEnum.WITHDRAW);
        Long memberId = item.getMemberId();

        OmsFarmStake stakeMember = getById(stakeMemberItem.getStakeMemberId());
        Assert.isTrue(stakeMember != null, "质押用户记录不存在");
        Assert.isTrue(stakeMember.getMemberId().equals(memberId), "质押用户不匹配");

        //更新质押记录
        Double itemAllocPoint = stakeMemberItem.getAllocPoint();
        Double memberAllocPoint = stakeMember.getAllocPoint();
        updateById(stakeMember
                .setAllocPoint(memberAllocPoint - itemAllocPoint)
                .setTotal(stakeMember.getTotal() - 1)
        );

        //删除质押记录
        stakeItemService.withdrawItem(stakeMemberItem);
        return stakeMemberItem;
    }
}
