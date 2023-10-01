package com.muling.mall.oms.converter;

import com.muling.mall.oms.pojo.entity.OmsOrder;
import com.muling.mall.oms.pojo.entity.OmsOrderDelivery;
import com.muling.mall.ums.pojo.dto.MemberAddressDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderDeliveryConverter {

    OrderDeliveryConverter INSTANCE = Mappers.getMapper(OrderDeliveryConverter.class);

    @Mappings({
            @Mapping(source = "consigneeName", target = "receiverName"),
            @Mapping(source = "consigneeMobile", target = "receiverPhone"),
            @Mapping(source = "province", target = "receiverProvince"),
            @Mapping(source = "city", target = "receiverCity"),
            @Mapping(source = "area", target = "receiverRegion"),
            @Mapping(source = "detailAddress", target = "receiverDetailAddress"),
            @Mapping(source = "zipCode", target = "receiverPostCode")
    })
    OmsOrderDelivery dto2Po(MemberAddressDTO memberAddressDTO);

}
