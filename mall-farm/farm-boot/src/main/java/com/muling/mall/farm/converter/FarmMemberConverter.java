package com.muling.mall.farm.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.mall.farm.pojo.entity.FarmMember;
import com.muling.mall.farm.pojo.vo.app.FarmMemberVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FarmMemberConverter {

    FarmMemberConverter INSTANCE = Mappers.getMapper(FarmMemberConverter.class);


    FarmMemberVO po2vo(FarmMember farmMember);


    Page<FarmMemberVO> entity2PageVO(Page<FarmMember> configsPage);


    default Long fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
