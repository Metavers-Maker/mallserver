package com.muling.mall.oms.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.internal.util.AlipaySignature;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.muling.common.constant.RedisConstants;
import com.muling.common.exception.BizException;
import com.muling.mall.oms.config.AlipayConfig;
import com.muling.mall.oms.enums.OrderStatusEnum;
import com.muling.mall.oms.pojo.entity.OmsOrder;
import com.muling.mall.oms.service.IAliPayCallBackService;
import com.muling.mall.oms.service.ICartService;
import com.muling.mall.oms.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
public class AliPayCallBackServiceImpl implements IAliPayCallBackService {

    private final ICartService cartService;
    private final IOrderService orderService;

    @Override
    public void handleAliPayOrderNotify(Map<String, String> params) throws Exception {

        try {
            //切记alipaypublickey是支付宝的公钥，请去open.alipay.com对应应用下查看。
            boolean flag = AlipaySignature.rsaCheckV1(params, AlipayConfig.aliPayPublicKey, AlipayConfig.charset, AlipayConfig.signType);
            if (!flag) {
                throw new BizException("验证失败");
            }
            log.info("支付宝回调验证成功:[{}]", JSON.toJSONString(params));
            // 商户订单号
            String out_trade_no = params.get("out_trade_no").toString();
            // 支付宝交易号
            String trade_no = params.get("trade_no").toString();
            // 根据商户订单号查询订单
            QueryWrapper<OmsOrder> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(OmsOrder::getOutTradeNo, out_trade_no).eq(OmsOrder::getStatus, OrderStatusEnum.PENDING_PAYMENT.getValue());
            OmsOrder orderDO = orderService.getOne(wrapper);
            // 支付成功处理
            String trade_status = params.get("trade_status");
            if ("TRADE_SUCCESS".equals(trade_status)) {
                orderDO.setTransactionId(trade_no);
                orderService.payOrderSuccess(orderDO);
            } else if ("WAIT_BUYER_PAY".equals(trade_status)) {
                //创建交易，等待买家付款
            } else if ("TRADE_CLOSED".equals(trade_status)) {
                //未付款交易超时关闭
            } else if ("TRADE_FINISHED".equals(trade_status)) {
                //交易结束，不可退款
            }
            log.info("账单更新成功");
            // 支付成功删除购物车已勾选的商品
            cartService.removeCheckedItem();
        } catch (Exception e) {
            log.error("支付通知处理异常:", e);
        }
    }
}
