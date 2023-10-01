package com.muling.mall.bms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.mall.bms.enums.StakeItemLogTypeEnum;
import com.muling.mall.bms.mapper.FarmLogMapper;
import com.muling.mall.bms.pojo.entity.OmsFarmLog;
import com.muling.mall.bms.pojo.entity.OmsFarmStakeItem;
import com.muling.mall.bms.service.IFarmLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class FarmLogServiceImpl extends ServiceImpl<FarmLogMapper, OmsFarmLog> implements IFarmLogService {


    @Override
    @Transactional
    public void stakeItemLog(OmsFarmStakeItem stakeItem, StakeItemLogTypeEnum logType) {

        OmsFarmLog stakeItemLog = new OmsFarmLog()
                .setMemberId(stakeItem.getMemberId())
                .setPoolId(stakeItem.getPoolId())
                .setSpuId(stakeItem.getSpuId())
                .setItemId(stakeItem.getId())
                .setItemNo(stakeItem.getItemNo())
                .setItemName(stakeItem.getItemName())
                .setPicUrl(stakeItem.getPicUrl())
                .setDays(stakeItem.getDays())
                .setCurrentDays(stakeItem.getCurrentDays())
                .setAllocPoint(stakeItem.getAllocPoint())
                .setLogType(logType);
        save(stakeItemLog);
    }
}
