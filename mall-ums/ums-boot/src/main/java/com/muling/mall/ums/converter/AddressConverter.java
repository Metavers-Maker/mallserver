package com.muling.mall.ums.converter;

import com.muling.mall.ums.pojo.dto.MemberAddressDTO;
import com.muling.mall.ums.pojo.entity.UmsAddress;
import com.muling.mall.ums.pojo.form.AddressForm;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AddressConverter {

    AddressConverter INSTANCE = Mappers.getMapper(AddressConverter.class);

    UmsAddress form2po(AddressForm addressForm);

//    MemberAddressDTO po2dto(UmsAddress umsAddress);

    List<MemberAddressDTO> pos2dtos(List<UmsAddress> umsAddresses);
}
