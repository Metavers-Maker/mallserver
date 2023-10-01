package com.muling.mall.ums.converter;

import com.muling.mall.ums.pojo.dto.MemberRealDTO;
import com.muling.mall.ums.pojo.entity.UmsMemberAuth;
import com.muling.mall.ums.pojo.vo.MemberAuthVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberAuthConverter {

    MemberAuthConverter INSTANCE = Mappers.getMapper(MemberAuthConverter.class);

    MemberRealDTO po2dto(UmsMemberAuth memberAuth);

    default Long fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
