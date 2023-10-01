package com.muling.mall.ums.listener;

import com.muling.common.constant.GlobalConstants;
import com.muling.mall.ums.service.IUmsMemberInviteService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class TeamAcitveValueListener {

    @Resource
    private IUmsMemberInviteService memberInviteService;

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(value = GlobalConstants.MQ_TEAM_ACTIVE_VALUE_QUEUE, durable = "true"),
                    exchange = @Exchange(value = GlobalConstants.MQ_TEAM_ACTIVE_VALUE_EXCHANGE),
                    key = GlobalConstants.MQ_TEAM_ACTIVE_VALUE_KEY
            )
    })
    @RabbitHandler
    public void process(String data, Message message, Channel channel) throws Exception {
        log.info("接收到消息：{}", data);
    }
}
