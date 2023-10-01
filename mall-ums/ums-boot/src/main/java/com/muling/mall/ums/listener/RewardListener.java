package com.muling.mall.ums.listener;

import cn.hutool.json.JSONUtil;
import com.muling.common.constant.GlobalConstants;
import com.muling.mall.ums.event.RewardEvent;
import com.muling.mall.ums.service.IUmsMemberService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class RewardListener {

    @Resource
    private IUmsMemberService memberService;

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(value = GlobalConstants.MQ_MEMBER_REWARD_QUEUE, durable = "true"),
                    exchange = @Exchange(value = GlobalConstants.MQ_MEMBER_REWARD_EXCHANGE),
                    key = GlobalConstants.MQ_MEMBER_REWARD_KEY
            )
    })
    @RabbitHandler
    public void process(String data, Message message, Channel channel) throws Exception {
        log.info("奖励日志：{}", data);
        RewardEvent rewardEvent = JSONUtil.toBean(data, RewardEvent.class);
    }
}
