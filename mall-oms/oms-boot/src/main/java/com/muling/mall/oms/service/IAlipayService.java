package com.muling.mall.oms.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;

public interface IAlipayService {

    /**
     * app端订单支付
     *
     * @param outTradeNo  订单编号
     * @param totalAmount 订单价格
     * @param subject     商品名称
     * @param body        商品内容
     */
    AlipayTradeAppPayResponse appPay(String outTradeNo, Integer totalAmount, String subject, String body) throws AlipayApiException;

    /**
     * 退款
     *
     * @param outTradeNo   订单编号
     * @param refundReason 退款原因
     * @param refundAmount 退款金额
     * @param outRequestNo 标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传
     */
    AlipayTradeRefundResponse refund(String outTradeNo, String refundReason, Integer refundAmount, String outRequestNo)
            throws AlipayApiException;

    /**
     * 交易查询
     *
     * @param outTradeNo 订单编号（唯一）
     */
    String query(String outTradeNo) throws AlipayApiException;

    /**
     * 交易关闭
     *
     * @param outTradeNo订单编号（唯一）
     */
    String close(String outTradeNo) throws AlipayApiException;

    /**
     * 退款查询
     *
     * @param outTradeNo   订单编号（唯一）
     * @param outRequestNo 标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传
     */
    String refundQuery(String outTradeNo, String outRequestNo) throws AlipayApiException;
}
