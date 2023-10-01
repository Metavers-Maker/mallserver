package com.muling.mall.bms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.bms.pojo.entity.OmsFarmStakeItem;

public interface IFarmStakeItemService extends IService<OmsFarmStakeItem> {


    public void stakeItem(OmsFarmStakeItem stakeItem);

    public void withdrawItem(OmsFarmStakeItem stakeItem);
}
