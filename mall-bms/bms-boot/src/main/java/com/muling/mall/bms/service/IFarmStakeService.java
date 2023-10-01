package com.muling.mall.bms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.bms.pojo.dto.StakeDTO;
import com.muling.mall.bms.pojo.entity.OmsFarmPool;
import com.muling.mall.bms.pojo.entity.OmsFarmStake;
import com.muling.mall.bms.pojo.entity.OmsFarmStakeItem;
import com.muling.mall.bms.pojo.entity.OmsMemberItem;

public interface IFarmStakeService extends IService<OmsFarmStake> {

    public void stake(OmsMemberItem item, StakeDTO stakeDTO, OmsFarmPool.Rule rule);

    public OmsFarmStakeItem withdraw(OmsMemberItem item, OmsFarmStakeItem stakeMemberItem);
}
