package com.muling.mall.task.listener;

import com.muling.common.constant.GlobalConstants;
import com.muling.mall.task.event.TaskCheckSuccessEvent;
import com.muling.mall.task.service.ITaskService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskCheckSuccessListener {


    private final ITaskService taskService;


    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(value = GlobalConstants.MQ_TASK_CHECK_SUCCESS_QUEUE, durable = "true"),
                    exchange = @Exchange(value = GlobalConstants.MQ_TASK_CHECK_SUCCESS_EXCHANGE),
                    key = GlobalConstants.MQ_TASK_CHECK_SUCCESS_KEY
            )
    })
    @RabbitHandler
    public void process(@Payload TaskCheckSuccessEvent event, Message message, Channel channel) throws Exception {
        log.info("审核成功：{}", event);
        try {
            taskService.checkSuccess(event);
        } catch (Exception e) {
            log.error("审核成功处理异常", e);
            throw e;
        }
    }
}
