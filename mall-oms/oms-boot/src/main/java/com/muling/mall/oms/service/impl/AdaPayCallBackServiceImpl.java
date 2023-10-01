package com.muling.mall.oms.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.muling.mall.oms.constant.OmsConstants;
import com.muling.mall.oms.enums.OrderStatusEnum;
import com.muling.mall.oms.pojo.entity.OmsOrder;
import com.muling.mall.oms.service.IAdaPayCallBackService;
import com.muling.mall.oms.service.ICartService;
import com.muling.mall.oms.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class AdaPayCallBackServiceImpl implements IAdaPayCallBackService {

    private final ICartService cartService;
    private final IOrderService orderService;

    private final RedissonClient redissonClient;

    @Override
    public void handleAdaPayOrderNotify(String data) throws Exception {

        JSONObject params = JSONUtil.parseObj(data);
        log.info("ADA回调验证成功:[{}]", JSON.toJSONString(params));

        // 商户订单号
        String out_trade_no = params.get("order_no").toString();
        // 支付宝交易号
        String trade_no = params.get("out_trans_id").toString();

        RLock lock = redissonClient.getLock(OmsConstants.PAY_CALLBACK_PREFIX + out_trade_no);
        try {
            lock.lock();

            // 根据商户订单号查询订单
            QueryWrapper<OmsOrder> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(OmsOrder::getOutTradeNo, out_trade_no).eq(OmsOrder::getStatus, OrderStatusEnum.PENDING_PAYMENT.getValue());
            OmsOrder orderDO = orderService.getOne(wrapper);
            // 支付成功处理
            String trade_status = params.getStr("status");
            if ("succeeded".equals(trade_status)) {
                orderDO.setStatus(OrderStatusEnum.PAYED.getValue());
                orderDO.setTransactionId(trade_no);
                orderService.payOrderSuccess(orderDO);

            }
            log.info("账单更新成功");
            // 支付成功删除购物车已勾选的商品
            cartService.removeCheckedItem();
        } catch (Exception e) {
            log.error("ADA支付通知处理异常:", e);
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}
