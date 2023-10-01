package com.muling.mall.bms.listener;

import com.muling.common.constant.GlobalConstants;
import com.muling.mall.bms.event.CreateMemberItemEvent;
import com.muling.mall.bms.event.TransMemberItemEvent;
import com.muling.mall.bms.event.TransPublishMemberItemEvent;
import com.muling.mall.bms.service.IMarketService;
import com.muling.mall.bms.service.IMemberItemService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderPaySuccessListener {

    private final IMemberItemService memberItemService;

    private final IMarketService marketService;

    /**
     * 首发市场物品购买
     */
    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(value = GlobalConstants.MQ_ORDER_PAY_SUCCESS_QUEUE, durable = "true"),
                    exchange = @Exchange(value = GlobalConstants.MQ_ORDER_PAY_SUCCESS_EXCHANGE),
                    key = GlobalConstants.MQ_ORDER_PAY_SUCCESS_KEY
            )
    })
    @RabbitHandler
    public void process(@Payload TransPublishMemberItemEvent event, Message message, Channel channel) throws Exception {
        log.info("订单支付成功：{}", event);
        try {
            List<String> itemNos = new ArrayList<>();
            TransPublishMemberItemEvent.ItemProperty itemProperty = event.getOrderItems().get(0);
            event.getOrderItems().forEach(item -> {
                itemNos.add(item.getItemNo());
            });
            memberItemService.unlockPublish(event.getMemberId(), itemProperty.getSpuId(), itemNos, true);
        } catch (Exception e) {
            log.error("订单支付成功处理异常", e);
            throw e;
        }
    }

    /**
     * 二级市场物品购买
     */
    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(value = GlobalConstants.MQ_ITEM_TRANS_SUCCESS_QUENE, durable = "true"),
                    exchange = @Exchange(value = GlobalConstants.MQ_ITEM_TRANS_SUCCESS_EXCHANGE),
                    key = GlobalConstants.MQ_ITEM_TRANS_SUCCESS_KEY
            )
    })
    @RabbitHandler
    public void processMarketBuy(@Payload TransMemberItemEvent event, Message message, Channel channel) throws Exception {
        log.info("订单支付成功：{}", event);
        try {
            marketService.buyItem(event);
        } catch (Exception e) {
            log.error("订单支付成功处理异常", e);
            throw e;
        }
    }
}
