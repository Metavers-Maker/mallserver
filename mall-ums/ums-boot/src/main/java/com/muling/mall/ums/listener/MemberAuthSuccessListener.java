package com.muling.mall.ums.listener;

import cn.hutool.json.JSONUtil;
import com.muling.common.constant.GlobalConstants;
import com.muling.mall.ums.event.MemberAuthSuccessEvent;
import com.muling.mall.ums.service.IUmsMemberAuthService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MemberAuthSuccessListener {

    private final IUmsMemberAuthService memberAuthService;

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(value = GlobalConstants.MQ_MEMBER_AUTH_SUCCESS_QUEUE, durable = "true"),
                    exchange = @Exchange(value = GlobalConstants.MQ_MEMBER_AUTH_SUCCESS_EXCHANGE),
                    key = GlobalConstants.MQ_MEMBER_AUTH_SUCCESS_KEY
            )
    })
    @RabbitHandler
    public void process(String data, Message message, Channel channel) throws Exception {
        log.info("认证成功：{}", data);
        MemberAuthSuccessEvent event = JSONUtil.toBean(data, MemberAuthSuccessEvent.class);
        //奖励
        memberAuthService.authReward(event);
    }
}
