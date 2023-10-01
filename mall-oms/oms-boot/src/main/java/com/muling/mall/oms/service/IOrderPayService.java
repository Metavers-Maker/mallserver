package com.muling.mall.oms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.oms.enums.PayTypeEnum;
import com.muling.mall.oms.pojo.entity.OmsOrderPay;
import com.muling.mall.oms.pojo.form.OrderCancelForm;

/**
 * 订单支付接口
 */
public interface IOrderPayService extends IService<OmsOrderPay> {


    public boolean isExistPaySn(String paySn);

    /**
     * 支付订单
     */
    <T> T pay(String ip, Long orderId, String appId, PayTypeEnum payTypeEnum, String channel);

    /**
     * 系统关闭订单
     */
    boolean closeOrder(String orderSn);

    /**
     * 取消订单
     */
    boolean cancelOrder(OrderCancelForm orderCancelForm);
}

