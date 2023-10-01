package com.muling.mall.bms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.mall.bms.enums.StakeItemLogTypeEnum;
import com.muling.mall.bms.mapper.StakeMemberItemMapper;
import com.muling.mall.bms.pojo.entity.OmsFarmStakeItem;
import com.muling.mall.bms.service.IFarmLogService;
import com.muling.mall.bms.service.IFarmStakeItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class FarmStakeItemServiceImpl extends ServiceImpl<StakeMemberItemMapper, OmsFarmStakeItem> implements IFarmStakeItemService {

    private final IFarmLogService stakeItemLogService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stakeItem(OmsFarmStakeItem stakeItem) {
        save(stakeItem);

        stakeItemLogService.stakeItemLog(stakeItem, StakeItemLogTypeEnum.STAKE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void withdrawItem(OmsFarmStakeItem stakeItem) {
        removeById(stakeItem.getId());
        stakeItemLogService.stakeItemLog(stakeItem, StakeItemLogTypeEnum.WITHDRAW);
    }
}
