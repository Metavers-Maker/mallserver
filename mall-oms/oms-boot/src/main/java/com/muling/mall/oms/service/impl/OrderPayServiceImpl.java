package com.muling.mall.oms.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderV3Result;
import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.huifu.adapay.core.exception.BaseAdaPayException;
import com.huifu.adapay.model.AdapayCommon;
import com.huifu.adapay.model.Payment;
import com.muling.common.base.IBaseEnum;
import com.muling.common.cert.service.HttpApiClientSand;
import com.muling.common.constant.RedisConstants;
import com.muling.common.exception.BizException;
import com.muling.common.redis.utils.BusinessNoGenerator;
import com.muling.common.util.IdUtils;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.bms.api.ItemFeignClient;
import com.muling.mall.bms.api.MarketFeignClient;
import com.muling.mall.oms.config.AdaPayConfig;
import com.muling.mall.oms.config.WxPayProperties;
import com.muling.mall.oms.constant.OmsConstants;
import com.muling.mall.oms.enums.OrderStatusEnum;
import com.muling.mall.oms.enums.PayTypeEnum;
import com.muling.mall.oms.mapper.OrderPayMapper;
import com.muling.mall.oms.pojo.entity.OmsOrder;
import com.muling.mall.oms.pojo.entity.OmsOrderItem;
import com.muling.mall.oms.pojo.entity.OmsOrderPay;
import com.muling.mall.oms.pojo.form.OrderCancelForm;
import com.muling.mall.oms.service.*;
import com.muling.mall.pms.api.SpuFeignClient;
import com.muling.mall.ums.api.MemberFeignClient;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 订单支付业务实现类
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class OrderPayServiceImpl extends ServiceImpl<OrderPayMapper, OmsOrderPay> implements IOrderPayService {

    private final RedissonClient redissonClient;
    private final IOrderService orderService;
    private final IOrderItemService orderItemService;
    private final MemberFeignClient memberFeignClient;
    private final WxPayProperties wxPayProperties;
    private final WxPayService wxPayService;
    private final IAlipayService alipayService;
    private final HttpApiClientSand clientSand;

    @Override
    public boolean isExistPaySn(String paySn) {
        return this.baseMapper.exists(Wrappers.<OmsOrderPay>lambdaQuery().eq(OmsOrderPay::getPaySn, paySn));
    }

    public static String changeF2Y(Integer price) {
        return BigDecimal.valueOf(price).divide(new BigDecimal(100)).toString();
    }

    public static String changeF2Y(Integer price, int scale) {
        return BigDecimal.valueOf(price).divide(new BigDecimal(100)).setScale(2).toString();
    }

    /**
     * 微信jsapi支付
     */
    private WxPayUnifiedOrderV3Result.JsapiResult wxJsapiPay(String appId, OmsOrder order) {
        Long memberId = MemberUtils.getMemberId();
        Long payAmount = order.getPayAmount();
        // 如果已经有outTradeNo了就先进行关单
        if (PayTypeEnum.WEIXIN_JSAPI.getValue().equals(order.getPayType()) && StrUtil.isNotBlank(order.getOutTradeNo())) {
            try {
                wxPayService.closeOrderV3(order.getOutTradeNo());
            } catch (WxPayException e) {
                log.error(e.getMessage(), e);
                throw new BizException("微信关单异常");
            }
        }
        String outTradeNo = IdUtils.makeOrderId(memberId, "wxo_");
        log.info("商户订单号拼接完成：{}", outTradeNo);
        // 更新订单状态
        order.setPayType(PayTypeEnum.WEIXIN_JSAPI.getValue());
        order.setOutTradeNo(outTradeNo);
        orderService.updateById(order);
        //
        String memberOpenId = memberFeignClient.getMemberOpenId(memberId).getData();
        WxPayUnifiedOrderV3Request wxRequest = new WxPayUnifiedOrderV3Request()
                .setOutTradeNo(outTradeNo)
                .setAppid(appId)
                .setNotifyUrl(wxPayProperties.getPayNotifyUrl())
                .setAmount(new WxPayUnifiedOrderV3Request.Amount().setTotal(Math.toIntExact(payAmount)))
                .setPayer(new WxPayUnifiedOrderV3Request.Payer().setOpenid(memberOpenId))
                .setDescription("赅买-订单编号" + order.getOrderSn());
        WxPayUnifiedOrderV3Result.JsapiResult jsapiResult;
        try {
            jsapiResult = wxPayService.createOrderV3(TradeTypeEnum.JSAPI, wxRequest);
        } catch (WxPayException e) {
            log.error(e.getMessage(), e);
            throw new BizException("微信统一下单异常");
        }
        return jsapiResult;
    }

    /**
     * 苹果支付
     */
    private Map<String, String> applePay(String appId, OmsOrder order) {
        Long memberId = MemberUtils.getMemberId();
        Long payAmount = order.getPayAmount();

        String outTradeNo = IdUtils.makeOrderId(memberId, "apo_");
        log.info("商户订单号拼接完成：{}", outTradeNo);
        // 更新订单状态
        order.setPayType(PayTypeEnum.APPLEPAY.getValue());
        order.setOutTradeNo(outTradeNo);
        orderService.updateById(order);
        //
        Map<String, String> params = new HashMap<>();
        params.put("orderId", order.getOrderSn());
        params.put("outTradeNo", outTradeNo);
        params.put("totalAmount", changeF2Y(order.getPayAmount().intValue()));
        return params;
    }

    /**
     * ada支付
     */
    private Map<String, Object> adaPay(String appId, OmsOrder order, String channel) {
        Long memberId = MemberUtils.getMemberId();
        Long payAmount = order.getPayAmount();
        List<OmsOrderItem> orderItems = orderItemService.getByOrderId(order.getId());
        String spuName = orderItems.get(0).getSpuName();
        if (StrUtil.isNotBlank(order.getOutTradeNo()) && order.getPayType() == PayTypeEnum.ADA_PAY.getValue()) {
            OmsOrderPay orderPay = this.getOne(new LambdaQueryWrapper<OmsOrderPay>().eq(OmsOrderPay::getPaySn, order.getOutTradeNo()));
            if (orderPay != null) {
                log.info("ADA支付订单返回：{}", orderPay.getCallbackContent());
                return JSONUtil.parseObj(orderPay.getCallbackContent());
            }
        }

        String outTradeNo = IdUtils.makeOrderId(memberId, "ado_");
        log.info("商户订单号拼接完成：{}", outTradeNo);
        // 更新订单状态
        order.setPayType(PayTypeEnum.ADA_PAY.getValue());
        order.setOutTradeNo(outTradeNo);
        orderService.updateById(order);

        Map<String, Object> stringObjectMap;
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            Map<String, Object> paymentParams = new HashMap<String, Object>(10);
            paymentParams.put("app_id", AdaPayConfig.appId);
            paymentParams.put("order_no", outTradeNo);
            paymentParams.put("pay_channel", channel);//AdaPayChannelEnum.ALIPAY_QR.getValue()
            paymentParams.put("pay_amt", changeF2Y(order.getPayAmount().intValue(), 2));
            paymentParams.put("goods_title", spuName);
            paymentParams.put("goods_desc", spuName);
            paymentParams.put("notify_url", AdaPayConfig.notifyUrl);
            stringObjectMap = Payment.create(paymentParams);
            //就是orderString 可以直接给客户端请求，无需再做处理。
            log.info("ADA支付订单返回：{}", JSONUtil.toJsonStr(stringObjectMap));
        } catch (BaseAdaPayException e) {
            log.error(e.getMessage(), e);
            throw new BizException("ADA统一下单异常");
        }
        //
        OmsOrderPay orderPay = OmsOrderPay.builder()
                .orderId(order.getId())
                .paySn(outTradeNo)
                .payAmount(payAmount)
                .payType(PayTypeEnum.ADA_PAY.getValue())
                .payStatus(OrderStatusEnum.PENDING_PAYMENT.getValue())
                .callbackContent(JSONUtil.toJsonStr(stringObjectMap))
                .callbackTime(new Date())
                .build();
        this.save(orderPay);
        return stringObjectMap;
    }

    /**
     * ada微信支付
     */
    private Map<String, Object> adaWeiXinPay(String appId, OmsOrder order) {
        Long memberId = MemberUtils.getMemberId();
        Long payAmount = order.getPayAmount();
        List<OmsOrderItem> orderItems = orderItemService.getByOrderId(order.getId());
        String spuName = orderItems.get(0).getSpuName();
        if (StrUtil.isNotBlank(order.getOutTradeNo()) && order.getPayType() == PayTypeEnum.ADA_PAY_WEI_XIN.getValue()) {
            OmsOrderPay orderPay = this.getOne(new LambdaQueryWrapper<OmsOrderPay>().eq(OmsOrderPay::getPaySn, order.getOutTradeNo()));
            if (orderPay != null) {
                log.info("ADA支付订单返回：{}", orderPay.getCallbackContent());
                return JSONUtil.parseObj(orderPay.getCallbackContent());
            }
        }
        //
        String outTradeNo = IdUtils.makeOrderId(memberId, "ado_");
        log.info("商户订单号拼接完成：{}", outTradeNo);
        // 更新订单状态
        order.setPayType(PayTypeEnum.ADA_PAY_WEI_XIN.getValue());
        order.setOutTradeNo(outTradeNo);
        orderService.updateById(order);
        //
        Map<String, Object> stringObjectMap;
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            Map<String, Object> paymentParams = new HashMap<String, Object>(10);
            paymentParams.put("app_id", AdaPayConfig.appId);
            paymentParams.put("order_no", outTradeNo);
//            paymentParams.put("pay_channel", "wx_lite-小程序支付");//AdaPayChannelEnum.ALIPAY_QR.getValue()
            paymentParams.put("adapay_func_code", "wxpay.createOrder");
            paymentParams.put("pay_amt", changeF2Y(order.getPayAmount().intValue(), 2));
            paymentParams.put("goods_title", spuName);
            paymentParams.put("goods_desc", spuName);
            paymentParams.put("notify_url", AdaPayConfig.notifyUrl);
            stringObjectMap = AdapayCommon.requestAdapayUits(paymentParams);
            //就是orderString 可以直接给客户端请求，无需再做处理。
            log.info("ADA支付订单返回：{}", JSONUtil.toJsonStr(stringObjectMap));
        } catch (BaseAdaPayException e) {
            log.error(e.getMessage(), e);
            throw new BizException("ADA统一下单异常");
        }
        //
        OmsOrderPay orderPay = OmsOrderPay.builder()
                .orderId(order.getId())
                .paySn(outTradeNo)
                .payAmount(payAmount)
                .payType(PayTypeEnum.ADA_PAY_WEI_XIN.getValue())
                .payStatus(OrderStatusEnum.PENDING_PAYMENT.getValue())
                .callbackContent(JSONUtil.toJsonStr(stringObjectMap))
                .callbackTime(new Date())
                .build();
        this.save(orderPay);
        return stringObjectMap;
    }

    /**
     * 杉德支付
     */
    private String sandPay(String ip, String appId, OmsOrder order) {
        Long memberId = MemberUtils.getMemberId();
        Long payAmount = order.getPayAmount();
        String outTradeNo = IdUtils.makeOrderId(memberId, "apo_");
        log.info("商户订单号拼接完成：{}", outTradeNo);
        // 更新订单状态
        order.setPayType(PayTypeEnum.SAND_PAY.getValue());
        order.setOutTradeNo(outTradeNo);
        orderService.updateById(order);
        String url = null;
        try {
            if (order.getOrderType() == 0) {
//            String userId,
//            String nickName,
//            String orderSn,
//            String goodsName,
//            Long payAmount,
//            boolean dev
                url = clientSand.c2bBuy(ip, "", "", order.getOrderSn(), order.getOrderName(), payAmount, false);
            } else if (order.getOrderType() == 1) {
//                String buyerUserId,
//                String sellerUserId,
//                String orderSn,
//                String goodsName,
//                Long payAmount,
//                BigDecimal fee,
//                boolean dev
                url = clientSand.c2cBuy(ip, "", "", order.getOrderSn(), order.getOrderName(), payAmount, order.getFeeCount().toString(), false);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BizException("Sand统一下单异常");
        }
        return url;
    }

    /**
     * 支付宝支付
     */
    private AlipayTradeAppPayResponse aliPay(String appId, OmsOrder order) {
        Long memberId = MemberUtils.getMemberId();
        Long payAmount = order.getPayAmount();
        List<OmsOrderItem> orderItems = orderItemService.getByOrderId(order.getId());
        String spuName = orderItems.get(0).getSpuName();
        if (StrUtil.isNotBlank(order.getOutTradeNo()) && order.getPayType() == PayTypeEnum.ALIPAY.getValue()) {
            OmsOrderPay orderPay = this.getOne(new LambdaQueryWrapper<OmsOrderPay>().eq(OmsOrderPay::getPaySn, order.getOutTradeNo()));
            if (orderPay != null) {
                log.info("支付宝支付订单返回：{}", orderPay.getCallbackContent());
                return JSONUtil.toBean(orderPay.getCallbackContent(), AlipayTradeAppPayResponse.class);
            }
        }
        //
        String outTradeNo = IdUtils.makeOrderId(memberId, "alio_");
        log.info("商户订单号拼接完成：{}", outTradeNo);
        // 更新订单状态
        order.setPayType(PayTypeEnum.ALIPAY.getValue());
        order.setOutTradeNo(outTradeNo);
        orderService.updateById(order);
        //
        AlipayTradeAppPayResponse response;
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            response = alipayService.appPay(outTradeNo, payAmount.intValue(), spuName, "赅买-订单编号" + order.getOrderSn());
            //就是orderString 可以直接给客户端请求，无需再做处理。
            log.info("支付宝支付订单返回：{}", response.getBody());
        } catch (AlipayApiException e) {
            log.error(e.getMessage(), e);
            throw new BizException("阿里统一下单异常");
        }

        OmsOrderPay orderPay = OmsOrderPay.builder()
                .orderId(order.getId())
                .paySn(outTradeNo)
                .payAmount(payAmount)
                .payType(PayTypeEnum.ALIPAY.getValue())
                .payStatus(OrderStatusEnum.PENDING_PAYMENT.getValue())
                .callbackContent(JSONUtil.toJsonStr(response))
                .callbackTime(new Date())
                .build();
        this.save(orderPay);

        return response;
    }

    /**
     * test 空投支付
     */
    private Map<String, String> airdropPay(String appId, OmsOrder order) {
        Long memberId = MemberUtils.getMemberId();
        Long payAmount = order.getPayAmount();
        String outTradeNo = IdUtils.makeOrderId(memberId, "apo_");
        log.info("商户订单号拼接完成：{}", outTradeNo);
        // 更新订单状态
        order.setPayType(PayTypeEnum.AIRDROP_PAY.getValue());
        order.setOutTradeNo(outTradeNo);
        orderService.updateById(order);
        Map<String, String> params = new HashMap<>();
        params.put("orderId", order.getOrderSn());
        params.put("outTradeNo", outTradeNo);
        params.put("totalAmount", changeF2Y(order.getPayAmount().intValue()));
        return params;
    }


    /**
     * 订单支付
     *
     * @return
     */
    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public <T> T pay(String ip, Long orderId, String appId, PayTypeEnum payTypeEnum, String channel) {
        OmsOrder order = orderService.getById(orderId);
        if (order == null) {
            throw new BizException("订单不存在");
        }
        RLock lock = redissonClient.getLock(OmsConstants.ORDER_SN_PREFIX + order.getOrderSn());
        try {
            lock.lock();
            T result;
            if (!OrderStatusEnum.PENDING_PAYMENT.getValue().equals(order.getStatus())) {
                throw new BizException("支付失败，请检查订单状态");
            }
            switch (payTypeEnum) {
                case WEIXIN_JSAPI:
                    result = (T) wxJsapiPay(appId, order);
                    break;
                case APPLEPAY:
                    result = (T) applePay(appId, order);
                    break;
                case ADA_PAY:
                    result = (T) adaPay(appId, order, channel);
                    break;
                case ADA_PAY_WEI_XIN:
                    result = (T) adaWeiXinPay(appId, order);
                    break;
                case SAND_PAY:
                    result = (T) sandPay(ip, appId, order);
                    break;
                case AIRDROP_PAY:
                    result = (T) airdropPay(appId, order);
                    break;
                case FREE_PAY:
                    result = (T) orderService.payOrderSuccess(order);
                    break;
                default:
                    result = (T) aliPay(appId, order);
                    break;
            }
            return result;
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    //

    /**
     * 系统自动关闭订单 or 主动关闭订单
     */
    @Override
    @Transactional
    public boolean closeOrder(String orderSn) {
        log.info("订单超时取消，orderSn:{}", orderSn);
        OmsOrder order = orderService.findByOrderSn(orderSn);
        if (order == null || !OrderStatusEnum.PENDING_PAYMENT.getValue().equals(order.getStatus())) {
            log.info("订单超时取消失败，orderSn:{} ,status:{}", orderSn, IBaseEnum.getEnumByValue(order.getStatus(), OrderStatusEnum.class).getLabel());
            return false;
        }
        // 如果已经有outTradeNo了就先进行关单
        if (PayTypeEnum.WEIXIN_JSAPI.getValue().equals(order.getPayType()) && StrUtil.isNotBlank(order.getOutTradeNo())) {
            try {
                wxPayService.closeOrderV3(order.getOutTradeNo());
            } catch (WxPayException e) {
                log.error(e.getMessage(), e);
                throw new BizException("微信关单异常");
            }
        } else if (PayTypeEnum.ALIPAY.getValue().equals(order.getPayType()) && StrUtil.isNotBlank(order.getOutTradeNo())) {
            try {
                alipayService.close(order.getOutTradeNo());
            } catch (AlipayApiException e) {
                log.error(e.getMessage(), e);
                throw new BizException("阿里关单异常");
            }
        }
        return orderService.closeOrder(order.getId());
    }

    /**
     * 取消订单
     */
    @Override
    public boolean cancelOrder(OrderCancelForm orderCancelForm) {
        Long memberId = MemberUtils.getMemberId();
        Long orderId = orderCancelForm.getOrderId();
        log.info("订单超时取消，订单ID：{}", orderId);
        OmsOrder order = orderService.findById(orderId);
        if (order == null) {
            throw new BizException("订单不存在");
        }
        if (order.getMemberId().longValue() != memberId.longValue()) {
            throw new BizException("订单不属于当前用户");
        }
        if (!OrderStatusEnum.PENDING_PAYMENT.getValue().equals(order.getStatus())) {
            throw new BizException("取消失败，订单状态不支持取消"); // 通过自定义异常，将异常信息抛出由异常处理器捕获显示给前端页面
        }
        // 如果已经有outTradeNo了就先进行关单
        if (PayTypeEnum.WEIXIN_JSAPI.getValue().equals(order.getPayType()) && StrUtil.isNotBlank(order.getOutTradeNo())) {
            try {
                wxPayService.closeOrderV3(order.getOutTradeNo());
            } catch (WxPayException e) {
                log.error(e.getMessage(), e);
                throw new BizException("微信关单异常");
            }
        } else if (PayTypeEnum.ALIPAY.getValue().equals(order.getPayType()) && StrUtil.isNotBlank(order.getOutTradeNo())) {
            try {
                alipayService.close(order.getOutTradeNo());
            } catch (AlipayApiException e) {
                log.error(e.getMessage(), e);
                throw new BizException("阿里关单异常");
            }
        }
        return orderService.cancelOrder(orderId, orderCancelForm.getReason());
    }

    //
}
