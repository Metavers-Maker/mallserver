package com.muling.mall.oms.listener;

import cn.hutool.json.JSONUtil;
import com.muling.common.constant.GlobalConstants;
import com.muling.mall.bms.event.ItemNoSyncEvent;
import com.muling.mall.oms.event.OrderCreateEvent;
import com.muling.mall.oms.service.IOrderItemService;
import com.muling.mall.oms.service.IOrderPayService;
import com.muling.mall.oms.service.IOrderService;
import com.muling.mall.pms.api.SkuFeignClient;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderListener {

    private final IOrderService orderService;
    private final IOrderPayService payService;

    private final IOrderItemService orderItemService;

    /**
     * 订单超时未支付，关闭订单，释放库存
     */
    @RabbitListener(queues = "order.close.queue")
    public void closeOrder(String orderSn, Message message, Channel channel) {
        log.info("=======================订单超时未支付，开始系统自动关闭订单=======================");
        try {
            if (payService.closeOrder(orderSn)) {
                log.info("=======================关闭订单成功，开始释放已锁定的库存=======================");
            } else {
                log.info("=======================关单失败，订单状态已处理，手动确认消息处理完毕=======================");
                // basicAck(tag,multiple)，multiple为true开启批量确认，小于tag值队列中未被消费的消息一次性确认
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
            }
        } catch (IOException e) {
            log.info("=======================系统自动关闭订单消息消费失败，重新入队=======================");
            try {
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            } catch (Exception ioException) {
                log.error("系统关单失败");
            }
        }
    }

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(value = GlobalConstants.MQ_ORDER_SECKILL_QUEUE, durable = "true"),
                    exchange = @Exchange(value = GlobalConstants.MQ_ORDER_SECKILL_EXCHANGE),
                    key = GlobalConstants.MQ_ORDER_SECKILL_KEY
            )
    })
    @RabbitHandler
    public void process(String content, Message message, Channel channel) throws Exception {
        log.info("=======================订单秒杀消息消费开始=======================");
        log.info("订单秒杀消息消费开始,消息内容：{}", content);
        try {
//            OrderCreateEvent orderCreateEvent = JSONUtil.toBean(content, OrderCreateEvent.class);
//            orderService.createSecKillOrder(orderCreateEvent);
        } catch (Exception e) {
            log.error("订单秒杀消息消费失败,{}", e.getMessage());
//            log.info("=======================系统自动秒杀订单消息消费失败，重新入队=======================");
//            try {
//                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
//            } catch (Exception ioException) {
//                log.error("系统秒杀订单失败");
//            }
        }
    }

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(value = GlobalConstants.MQ_ITEM_BUY1_SUCCESS_QUENE, durable = "true"),
                    exchange = @Exchange(value = GlobalConstants.MQ_ITEM_BUY1_SUCCESS_EXCHANGE),
                    key = GlobalConstants.MQ_ITEM_BUY1_SUCCESS_KEY
            )
    })
    @RabbitHandler
    public void processBuy1(String content, Message message, Channel channel) throws Exception {
        log.info("1级市场购买完毕，物品ID同步：{}", content);
        try {
            ItemNoSyncEvent itemNoSyncEvent = JSONUtil.toBean(content, ItemNoSyncEvent.class);
            orderItemService.itemNoSync(itemNoSyncEvent);

        } catch (Exception e) {
            log.error("1级市场购买完毕，物品ID同步,{}", e.getMessage());
        }
    }
    //
}
