package com.muling.mall.bms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.bms.enums.StakeItemLogTypeEnum;
import com.muling.mall.bms.pojo.entity.OmsFarmLog;
import com.muling.mall.bms.pojo.entity.OmsFarmStakeItem;

public interface IFarmLogService extends IService<OmsFarmLog> {

    public void stakeItemLog(OmsFarmStakeItem stakeItem, StakeItemLogTypeEnum logType);
}
