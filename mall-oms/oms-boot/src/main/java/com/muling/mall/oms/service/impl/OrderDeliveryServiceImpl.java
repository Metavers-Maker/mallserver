package com.muling.mall.oms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.mall.oms.converter.OrderDeliveryConverter;
import com.muling.mall.oms.mapper.OrderDeliveryMapper;
import com.muling.mall.oms.pojo.entity.OmsOrder;
import com.muling.mall.oms.pojo.entity.OmsOrderDelivery;
import com.muling.mall.oms.service.IOrderDeliveryService;
import com.muling.mall.ums.pojo.dto.MemberAddressDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("orderDeliveryService")
public class OrderDeliveryServiceImpl extends ServiceImpl<OrderDeliveryMapper, OmsOrderDelivery> implements IOrderDeliveryService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(MemberAddressDTO memberAddressDTO, OmsOrder order) {
        OmsOrderDelivery omsOrderDelivery = OrderDeliveryConverter.INSTANCE.dto2Po(memberAddressDTO);
        omsOrderDelivery.setOrderId(order.getId());
        omsOrderDelivery.setRemark(order.getRemark());
        this.save(omsOrderDelivery);
    }

}
