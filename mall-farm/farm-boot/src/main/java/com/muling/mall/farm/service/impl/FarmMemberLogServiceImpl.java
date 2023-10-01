package com.muling.mall.farm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.mall.farm.converter.FarmMemberItemLogConverter;
import com.muling.mall.farm.enums.FarmOpEnum;
import com.muling.mall.farm.mapper.FarmMemberLogMapper;
import com.muling.mall.farm.pojo.entity.FarmMember;
import com.muling.mall.farm.pojo.entity.FarmMemberItem;
import com.muling.mall.farm.pojo.entity.FarmMemberLog;
import com.muling.mall.farm.service.IFarmMemberLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FarmMemberLogServiceImpl extends ServiceImpl<FarmMemberLogMapper, FarmMemberLog> implements IFarmMemberLogService {


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(FarmMember farmMember, FarmOpEnum farmOpEnum) {

        FarmMemberLog farmMemberItemLog = FarmMemberItemLogConverter.INSTANCE.po2log(farmMember);
        farmMemberItemLog.setType(farmOpEnum.getValue());
        save(farmMemberItemLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(FarmMemberItem farmMemberItem, FarmOpEnum farmOpEnum) {

        FarmMemberLog farmMemberItemLog = FarmMemberItemLogConverter.INSTANCE.po2log(farmMemberItem);
        farmMemberItemLog.setType(farmOpEnum.getValue());
        save(farmMemberItemLog);
    }
}
