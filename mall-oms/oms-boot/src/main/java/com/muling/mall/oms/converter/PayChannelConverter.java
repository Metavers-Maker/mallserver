package com.muling.mall.oms.converter;

import com.muling.mall.oms.pojo.entity.OmsPayChannel;
import com.muling.mall.oms.pojo.form.ChannelForm;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PayChannelConverter {

    PayChannelConverter INSTANCE = Mappers.getMapper(PayChannelConverter.class);

//    @Mappings({
//            @Mapping(source = "linkType.value", target = "linkType")
//    })
//    BannerVO do2vo(PmsBanner banner);

    OmsPayChannel form2po(ChannelForm channelForm);

    void updatePo(ChannelForm channelForm, @MappingTarget OmsPayChannel channel);


}
