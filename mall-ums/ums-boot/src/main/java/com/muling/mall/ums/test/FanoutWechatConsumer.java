package com.muling.mall.ums.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FanoutWechatConsumer {

    @RabbitListener(queues = {"fanout.wechat.queue"})
    @RabbitHandler
    public void receiveMessage(@Payload Message message) {
        log.info("微信订单信息====" + message);
    }
}
