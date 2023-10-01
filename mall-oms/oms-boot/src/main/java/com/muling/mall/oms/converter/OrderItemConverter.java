package com.muling.mall.oms.converter;

import com.muling.mall.bms.event.TransMemberItemEvent;
import com.muling.mall.bms.event.TransPublishMemberItemEvent;
import com.muling.mall.oms.event.OrderCreateEvent;
import com.muling.mall.oms.pojo.entity.OmsOrder;
import com.muling.mall.oms.pojo.entity.OmsOrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderItemConverter {

    OrderItemConverter INSTANCE = Mappers.getMapper(OrderItemConverter.class);


    OmsOrder event2Po(OrderCreateEvent orderCreateEvent);

    List<TransPublishMemberItemEvent.ItemProperty> po2voList(List<OmsOrderItem> orderItems);

    List<TransMemberItemEvent.ItemProperty> po2voListMarket(List<OmsOrderItem> orderItems);


}
