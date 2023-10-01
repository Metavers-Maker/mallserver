package com.muling.mall.ums.listener;

import cn.hutool.json.JSONUtil;
import com.muling.common.constant.GlobalConstants;
import com.muling.mall.ums.event.MemberRegisterEvent;
import com.muling.mall.ums.event.OhMemberRegisterEvent;
import com.muling.mall.ums.service.IUmsMemberInviteService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MemberRegisterListener {

    private final IUmsMemberInviteService inviteService;

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(value = GlobalConstants.MQ_MEMBER_REGISTER_QUEUE, durable = "true"),
                    exchange = @Exchange(value = GlobalConstants.MQ_MEMBER_REGISTER_EXCHANGE),
                    key = GlobalConstants.MQ_MEMBER_REGISTER_KEY
            )
    })
    @RabbitHandler
    public void process(String data, Message message, Channel channel) throws Exception {
        log.info("注册日志：{}", data);
        MemberRegisterEvent event = JSONUtil.toBean(data, MemberRegisterEvent.class);
        inviteService.addInvite(event);
    }

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(value = GlobalConstants.MQ_OH_MEMBER_REGISTER_QUEUE, durable = "true"),
                    exchange = @Exchange(value = GlobalConstants.MQ_OH_MEMBER_REGISTER_QUEUE),
                    key = GlobalConstants.MQ_MEMBER_REGISTER_KEY
            )
    })
    @RabbitHandler
    public void processOH(String data, Message message, Channel channel) throws Exception {
        log.info("OH注册日志：{}", data);
    }
}
