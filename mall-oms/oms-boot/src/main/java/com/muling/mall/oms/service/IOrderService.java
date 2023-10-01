package com.muling.mall.oms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.mall.oms.enums.PayTypeEnum;
import com.muling.mall.oms.event.OrderCreateEvent;
import com.muling.mall.oms.pojo.entity.OmsOrder;
import com.muling.mall.oms.pojo.form.*;
import com.muling.mall.oms.pojo.query.OrderPageQuery;
import com.muling.mall.oms.pojo.vo.OrderConfirmVO;
import com.muling.mall.oms.pojo.vo.OrderPageVO;
import com.muling.mall.oms.pojo.vo.OrderSubmitVO;

import java.util.Map;

/**
 * 订单业务接口
 */
public interface IOrderService extends IService<OmsOrder> {

    /**
     * 首发市场订单签发（sn）
     */
    OrderConfirmVO confirm(OrderConfirmForm orderConfirmForm);

    /**
     * 首发市场订单提交
     */
    OrderSubmitVO submit(OrderSubmitForm orderSubmitForm);

    /**
     * 寄售市场订单签发（sn）
     */
    OrderConfirmVO confirmMarket(OrderM2ConfirmForm orderM2ConfirmForm);

    /**
     * 寄售市场订单提交
     */
    OrderSubmitVO submitMarket(OrderSubmitForm orderSubmitForm);

    /**
     * (System)关闭订单
     */
    boolean closeOrder(Long orderId);

    /**
     * (App)取消订单接口
     */
    boolean cancelOrder(Long orderId, String reason);

    /**
     * 退款
     *
     * @param orderId
     * @return
     */
    boolean refundOrder(Long orderId);

    /**
     * 申请退款
     *
     * @param orderRefundForm
     * @return
     */
    boolean applyRefundOrder(OrderRefundForm orderRefundForm);

    /**
     * 取消申请退款
     *
     * @param orderId
     * @return
     */
    boolean cancelApplyRefundOrder(Long orderId);

    /**
     * 拒绝申请退款
     *
     * @param orderId
     * @return
     */
    boolean rejectApplyRefundOrder(Long orderId);

    /**
     * 获得订单
     *
     * @param id
     * @return
     */
    OmsOrder findById(Long id);

    /**
     * 获得订单
     *
     * @param orderSn
     * @return
     */
    OmsOrder findByOrderSn(String orderSn);

    /**
     * 删除订单
     */
    boolean deleteOrder(Long id);

    /**
     * 订单分页列表
     *
     * @param queryParams
     * @return
     */
    IPage<OrderPageVO> listOrderPages(OrderPageQuery queryParams);

    /**
     * 判断流水是否已经存在
     *
     * @param transactionId
     * @return
     */
    public boolean isExistTransactionId(String transactionId);

    /**
     * 根据outTradeNo和状态查询订单
     *
     * @param orderSn
     * @return
     */
    OmsOrder getByOutTradeNoAndStatus(String orderSn, Integer status);


    /**
     * 支付成功后续
     *
     * @param order
     */
    public Map<String, String> payOrderSuccess(OmsOrder order);
}

