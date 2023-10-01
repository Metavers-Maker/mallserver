package com.muling.mall.farm.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.farm.enums.FarmOpEnum;
import com.muling.mall.farm.pojo.entity.FarmMember;
import com.muling.mall.farm.pojo.entity.FarmMemberItem;
import com.muling.mall.farm.pojo.entity.FarmMemberLog;

public interface IFarmMemberLogService extends IService<FarmMemberLog> {

    public void save(FarmMember farmMember, FarmOpEnum farmOpEnum);

    public void save(FarmMemberItem farmMemberItem, FarmOpEnum farmOpEnum);
}
