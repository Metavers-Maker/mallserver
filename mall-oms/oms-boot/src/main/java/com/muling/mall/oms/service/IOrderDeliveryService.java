package com.muling.mall.oms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.oms.pojo.entity.OmsOrder;
import com.muling.mall.oms.pojo.entity.OmsOrderDelivery;
import com.muling.mall.ums.pojo.dto.MemberAddressDTO;

/**
 * 订单物流记录表
 *
 * @author huawei
 * @email huawei_code@163.com
 * @date 2020-12-30 22:31:10
 */
public interface IOrderDeliveryService extends IService<OmsOrderDelivery> {

    public void save(MemberAddressDTO memberAddressDTO, OmsOrder order);
}

