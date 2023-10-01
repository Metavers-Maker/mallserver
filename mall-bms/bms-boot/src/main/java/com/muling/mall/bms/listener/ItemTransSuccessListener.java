package com.muling.mall.bms.listener;

import com.muling.common.constant.GlobalConstants;
import com.muling.mall.bms.event.ChainTransMemberItemEvent;
import com.muling.mall.bms.event.CreateMemberItemEvent;
import com.muling.mall.bms.event.ItemMintEvent;
import com.muling.mall.bms.event.TransMemberItemEvent;
import com.muling.mall.bms.service.IMarketService;
import com.muling.mall.bms.service.IMemberItemService;
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
public class ItemTransSuccessListener {

    private final IMemberItemService memberItemService;

    /**
     * 物品铸造
     */
    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(value = GlobalConstants.MQ_ITEM_CHAIN_MINT_SUCCESS_QUENE, durable = "true"),
                    exchange = @Exchange(value = GlobalConstants.MQ_ITEM_CHAIN_MINT_SUCCESS_EXCHANGE),
                    key = GlobalConstants.MQ_ITEM_CHAIN_MINT_SUCCESS_KEY
            )
    })
    @RabbitHandler
    public void mintProcess(@Payload ItemMintEvent event, Message message, Channel channel) throws Exception {
        log.info("物品铸造成功 链上同步：{}", event);
        try {
//            memberItemService.createItem(event);
        } catch (Exception e) {
            log.error("物品铸造处理异常", e);
            throw e;
        }
    }

    /**
     * 物品转移成功
     */
    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(value = GlobalConstants.MQ_ITEM_CHAIN_TRANS_SUCCESS_QUENE, durable = "true"),
                    exchange = @Exchange(value = GlobalConstants.MQ_ITEM_CHAIN_TRANS_SUCCESS_EXCHANGE),
                    key = GlobalConstants.MQ_ITEM_CHAIN_TRANS_SUCCESS_KEY
            )
    })
    @RabbitHandler
    public void process(@Payload ChainTransMemberItemEvent event, Message message, Channel channel) throws Exception {
        log.info("物品转移成功 链上同步：{}", event);
        try {
//            memberItemService.createItem(event);
        } catch (Exception e) {
            log.error("物品转移处理异常", e);
            throw e;
        }
    }

}
