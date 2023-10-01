package com.muling.mall.oms.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.*;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.muling.mall.oms.config.AlipayConfig;
import com.muling.mall.oms.service.IAlipayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlipayServiceImpl implements IAlipayService {

    private final AlipayClient alipayClient;

    private final AlipayConfig alipayConfig;

    /**
     * 退款
     *
     * @param outTradeNo   订单编号
     * @param refundReason 退款原因
     * @param refundAmount 退款金额
     * @param outRequestNo 标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传
     */
    @Override
    public AlipayTradeRefundResponse refund(String outTradeNo, String refundReason, Integer refundAmount, String outRequestNo)
            throws AlipayApiException {
        AlipayTradeRefundRequest alipayRequest = new AlipayTradeRefundRequest();
        String changeF2Y = changeF2Y(refundAmount);
        /** 调取接口 */
        alipayRequest.setBizContent("{\"out_trade_no\":\"" + outTradeNo + "\"," + "\"refund_amount\":\"" + changeF2Y
                + "\"," + "\"refund_reason\":\"" + refundReason + "\"," + "\"out_request_no\":\"" + outRequestNo
                + "\"}");
        AlipayTradeRefundResponse response = alipayClient.execute(alipayRequest);
        return response;
    }

    /**
     * 交易查询
     *
     * @param outTradeNo 订单编号（唯一）
     */
    @Override
    public String query(String outTradeNo) throws AlipayApiException {
        AlipayTradeQueryRequest alipayRequest = new AlipayTradeQueryRequest();
        /** 请求接口 */
        JSONObject bizContent = JSONUtil.createObj();
        bizContent.set("out_trade_no", outTradeNo);
        alipayRequest.setBizContent(bizContent.toString());
        /** 转换格式 */
        String result = alipayClient.execute(alipayRequest).getBody();
        return result;
    }

    /**
     * 交易关闭
     *
     * @param outTradeNo 订单编号（唯一）
     */
    @Override
    public String close(String outTradeNo) throws AlipayApiException {
        AlipayTradeCloseRequest alipayRequest = new AlipayTradeCloseRequest();
        JSONObject bizContent = JSONUtil.createObj();
        bizContent.set("out_trade_no", outTradeNo);
        alipayRequest.setBizContent(bizContent.toString());

        String result = alipayClient.execute(alipayRequest).getBody();

        return result;
    }

    /**
     * 退款查询
     *
     * @param outTradeNo   订单编号（唯一）
     * @param outRequestNo 标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传
     */
    @Override
    public String refundQuery(String outTradeNo, String outRequestNo) throws AlipayApiException {
        AlipayTradeFastpayRefundQueryRequest alipayRequest = new AlipayTradeFastpayRefundQueryRequest();

        /** 请求接口 */
        alipayRequest.setBizContent(
                "{\"out_trade_no\":\"" + outTradeNo + "\"," + "\"out_request_no\":\"" + outRequestNo + "\"}");

        /** 格式转换 */
        String result = alipayClient.execute(alipayRequest).getBody();

        return result;
    }

    /**
     * app端订单支付
     *
     * @param outTradeNo  订单编号
     * @param payAmount 订单价格
     * @param subject     商品名称
     */
    @Override
    public AlipayTradeAppPayResponse appPay(String outTradeNo, Integer payAmount, String subject, String body) throws AlipayApiException {
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();

//        /** 同步通知，支付完成后，支付成功页面 */
//        alipayRequest.setReturnUrl(AlipayConfig.return_url);
        /** 异步通知，支付完成后，需要进行的异步处理 */
        alipayRequest.setNotifyUrl(alipayConfig.getNotifyUrl());

        /** 销售产品码（固定） */
        String productCode = "QUICK_MSECURITY_PAY";

        /** 进行赋值 */
        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        //商品名称
        model.setSubject(subject);
        model.setBody(body);
        model.setOutTradeNo(outTradeNo);
        model.setTimeoutExpress("15m");
        model.setTotalAmount(changeF2Y(payAmount));
        model.setProductCode(productCode);
        request.setBizModel(model);
        request.setNotifyUrl(AlipayConfig.notifyUrl);

        /** 格式转换 */
        AlipayTradeAppPayResponse result = alipayClient.sdkExecute(request);
        return result;
    }

    public BigDecimal changeY2F(Integer fee) {
        BigDecimal feeNo = new BigDecimal(fee);//string 转 BigDecimal 分转元
        return feeNo.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);//分转元
    }

    public static String changeF2Y(Integer price) {
        return BigDecimal.valueOf(price).divide(new BigDecimal(100)).toString();
    }
}
