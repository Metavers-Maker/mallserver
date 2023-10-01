package com.muling.mall.ums.converter;

import com.muling.mall.ums.pojo.dto.MemberWhiteDTO;
import com.muling.mall.ums.pojo.entity.UmsWhite;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberWhiteConverter {

    MemberWhiteConverter INSTANCE = Mappers.getMapper(MemberWhiteConverter.class);

    MemberWhiteDTO po2dto(UmsWhite umsWhite);

}
