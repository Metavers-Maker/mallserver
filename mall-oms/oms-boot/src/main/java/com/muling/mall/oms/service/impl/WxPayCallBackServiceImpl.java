package com.muling.mall.oms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.binarywang.wxpay.bean.notify.SignatureHeader;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyV3Result;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyV3Result;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.muling.mall.oms.enums.OrderStatusEnum;
import com.muling.mall.oms.pojo.entity.OmsOrder;
import com.muling.mall.oms.service.ICartService;
import com.muling.mall.oms.service.IOrderService;
import com.muling.mall.oms.service.IWxPayCallBackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class WxPayCallBackServiceImpl implements IWxPayCallBackService {

    private final WxPayService wxPayService;
    private final ICartService cartService;
    private final IOrderService orderService;

    @Override
    public void handleWxPayOrderNotify(SignatureHeader signatureHeader, String notifyData) throws WxPayException {
        log.info("开始处理支付结果通知");
        // 解密支付通知内容
        final WxPayOrderNotifyV3Result.DecryptNotifyResult result =
                this.wxPayService.parseOrderNotifyV3Result(notifyData, signatureHeader).getResult();
        log.debug("支付通知解密成功：[{}]", result.toString());

        // 根据商户订单号查询订单
        QueryWrapper<OmsOrder> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(OmsOrder::getOutTradeNo, result.getOutTradeNo());
        OmsOrder orderDO = orderService.getOne(wrapper);
        // 支付成功处理
        if (WxPayConstants.WxpayTradeStatus.SUCCESS.equals(result.getTradeState())) {
            orderDO.setTransactionId(result.getTransactionId());
            orderService.payOrderSuccess(orderDO);
        }
        log.info("账单更新成功");
        // 支付成功删除购物车已勾选的商品
        cartService.removeCheckedItem();
    }

    @Override
    public void handleWxPayRefundNotify(SignatureHeader signatureHeader, String notifyData) throws WxPayException {
        log.info("开始处理退款结果通知");
        // 解密支付通知内容
        final WxPayRefundNotifyV3Result.DecryptNotifyResult result =
                this.wxPayService.parseRefundNotifyV3Result(notifyData, signatureHeader).getResult();
        log.debug("退款通知解密成功：[{}]", result.toString());
        // 根据商户退款单号查询订单
        QueryWrapper<OmsOrder> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(OmsOrder::getOutTradeNo, result.getOutTradeNo());
        OmsOrder orderDO = orderService.getOne(wrapper);
        // 退款成功处理
        if (WxPayConstants.RefundStatus.SUCCESS.equals(result.getRefundStatus())) {
            orderDO.setStatus(OrderStatusEnum.REFUNDED.getValue());
            orderDO.setRefundId(result.getRefundId());
            orderService.updateById(orderDO);
        }
        log.info("账单更新成功");
    }
}
