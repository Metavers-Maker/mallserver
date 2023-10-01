package com.muling.mall.oms.service.impl;

import com.muling.mall.oms.enums.OrderStatusEnum;
import com.muling.mall.oms.pojo.dto.ApplePayCallBackDTO;
import com.muling.mall.oms.pojo.entity.OmsOrder;
import com.muling.mall.oms.pojo.entity.OmsOrderItem;
import com.muling.mall.oms.service.IApplePayService;
import com.muling.mall.oms.service.IOrderItemService;
import com.muling.mall.oms.service.IOrderService;
import com.muling.mall.oms.util.ApplePayCallBackResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class ApplePayServiceImpl implements IApplePayService {

    private final IOrderService orderService;

    private final IOrderItemService orderItemService;

    @Transactional
    public void commit(ApplePayCallBackDTO callBackDTO, ApplePayCallBackResponse.InApp inApp) {
        boolean existTransactionId = orderService.isExistTransactionId(callBackDTO.getTransactionId());
        if (existTransactionId) {
            log.info("订单已经处理过了,transactionId:{}", callBackDTO.getTransactionId());
            return;
        }
        OmsOrder orderDO = orderService.getByOutTradeNoAndStatus(callBackDTO.getOutTradeNo(), OrderStatusEnum.PENDING_PAYMENT.getValue());
        if (orderDO == null) {
            log.error("订单不存在,outTradeNo:{}", callBackDTO.getOutTradeNo());
            return;
        }
        OmsOrderItem orderItem = orderItemService.getByOrderId(orderDO.getId()).get(0);
        Long skuId = orderItem.getSkuId();
        if (!inApp.getProduct_id().equals(orderItem.getProductId())) {
            log.error("商品不匹配:{}", skuId);
            return;
        }
        // 支付成功
        if (inApp.getTransaction_id().equals(callBackDTO.getTransactionId())) {
            orderDO.setTransactionId(callBackDTO.getTransactionId());
            orderService.payOrderSuccess(orderDO);
        }
    }
}
