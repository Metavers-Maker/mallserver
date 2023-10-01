package com.muling.mall.pms.controller.app;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.github.binarywang.wxpay.exception.WxPayException;
//import com.muling.common.constant.GlobalConstants;
//import com.muling.common.exception.BizException;
//import com.muling.mall.oms.constant.OmsConstants;
//import com.muling.mall.oms.enums.OrderStatusEnum;
//import com.muling.mall.oms.enums.PayTypeEnum;
import com.muling.mall.pms.pojo.dto.BSNChainCallBackDTO;
//import com.muling.mall.oms.pojo.entity.OmsOrder;
//import com.muling.mall.oms.pojo.entity.OmsOrderPay;
//import com.muling.mall.oms.service.IOrderPayService;
//import com.muling.mall.oms.service.IOrderService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Objects;

/**
 * 苹果回调接口
 */
@Api(tags = "app-BSNCHAIN回调接口")
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/callback-api/v1/bsn-chain")
public class BSNChainCallbackController {

//    private final IOrderService orderService;

//    private final IOrderPayService orderPayService;

    private final RedissonClient redissonClient;

    private final RabbitTemplate rabbitTemplate;

    /**
     * 文昌链回调接口
     *
     * @param callBackDTO
     * @return
     * @throws
     */
    @PostMapping(value = "/notify-order", produces = MediaType.TEXT_PLAIN_VALUE)
    public String applePayOrderNotify(@RequestBody BSNChainCallBackDTO callBackDTO) {
//        String input = JSON.toJSONString(callBackDTO);
//        log.info("开始处理支付结果通知:{}", input);
        try {
//            RLock lock = redissonClient.getLock(OmsConstants.PAY_CALLBACK_PREFIX + callBackDTO.getTransactionId());
//            try {
//                lock.lock();
//
//                boolean existPaySn = orderPayService.isExistPaySn(callBackDTO.getOutTradeNo());
//                if (existPaySn) {
//                    throw new BizException("重新提交");
//                }
//                OmsOrder order = orderService.getById(callBackDTO.getOrderId());
//                OmsOrderPay orderPay = OmsOrderPay.builder()
//                        .orderId(callBackDTO.getOrderId())
//                        .paySn(callBackDTO.getOutTradeNo())
//                        .payAmount(order.getPayAmount())
//                        .payType(PayTypeEnum.APPLEPAY.getValue())
//                        .payStatus(OrderStatusEnum.PENDING_PAYMENT.getValue())
//                        .callbackContent(input)
//                        .callbackTime(new Date())
//                        .build();
//                orderPayService.save(orderPay);
//                rabbitTemplate.convertAndSend(GlobalConstants.MQ_APPLE_PAY_QUEUE, input);
//            } finally {
//                //释放锁
//                if (lock.isLocked() && lock.isHeldByCurrentThread()) {
//                    lock.unlock();
//                }
//            }
        } catch (Exception e) {
            log.error("BSN回调处理异常", e);
            throw e;
        }
        //成功
        return "success";
    }

    @PostMapping("/refund")
    public void refund(@RequestBody BSNChainCallBackDTO refundIosDTO) {
//        log.info("苹果退款请求参数:{}", JSONUtil.toJsonStr(refundIosDTO));
//        if (refundIosDTO.getNotification_type().equals("REFUND")) {
//            for (ApplePayRefundIosDTO.LatestReceiptInfo lr : refundIosDTO.getUnified_receipt().getLatest_receipt_info()) {
//                QueryWrapper<OmsOrder> wrapper = new QueryWrapper<OmsOrder>();
//                wrapper.eq("transaction_id", lr.getTransaction_id());
//                wrapper.eq("status", OrderStatusEnum.REFUNDED.getValue());
//                OmsOrder order = orderService.getOne(wrapper);
//                if (Objects.isNull(order)) {
//                    log.info("订单不存在或已退款,transactionId:{}", lr.getTransaction_id());
//                    break;
//                }
//
//                log.info("订单退款，订单ID：{}", order.getId());
//                RLock lock = redissonClient.getLock(OmsConstants.ORDER_ID_PREFIX + order.getId());
//                try {
//                    lock.lock();
//                    if (!OrderStatusEnum.APPLY_REFUND.getValue().equals(order.getStatus())) {
//                        throw new BizException("退款失败，订单状态不支持退款"); // 通过自定义异常，将异常信息抛出由异常处理器捕获显示给前端页面
//                    }
//
//                    order.setStatus(OrderStatusEnum.REFUNDED.getValue());
//                    order.setOutRefundNo(lr.getTransaction_id());
//                    boolean result = orderService.updateById(order);
//
//                    //退款需要后续删除物品
//                } finally {
//                    //释放锁
//                    if (lock.isLocked()) {
//                        lock.unlock();
//                    }
//                }
//
//            }
//        }
    }
}
