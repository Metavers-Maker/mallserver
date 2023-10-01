package com.muling.mall.oms.converter;

import com.muling.mall.oms.dto.OrderDTO;
import com.muling.mall.oms.event.OrderCreateEvent;
import com.muling.mall.oms.pojo.entity.OmsOrder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderConverter {

    OrderConverter INSTANCE = Mappers.getMapper(OrderConverter.class);


    OmsOrder event2Po(OrderCreateEvent orderCreateEvent);

    OrderDTO eniity2Dto(OmsOrder order);
}
