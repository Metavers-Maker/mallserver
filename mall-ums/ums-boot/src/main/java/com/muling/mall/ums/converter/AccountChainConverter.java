package com.muling.mall.ums.converter;

import com.muling.mall.ums.pojo.dto.MemberAccountChainDTO;
import com.muling.mall.ums.pojo.entity.UmsAccountChain;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountChainConverter {

    AccountChainConverter INSTANCE = Mappers.getMapper(AccountChainConverter.class);

    List<MemberAccountChainDTO> pos2dtos(List<UmsAccountChain> umsAddresses);
}
