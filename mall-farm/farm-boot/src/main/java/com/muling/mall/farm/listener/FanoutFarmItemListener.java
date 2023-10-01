package com.muling.mall.farm.listener;

import com.muling.mall.farm.service.IFarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FanoutFarmItemListener {

    private final IFarmService farmService;

    @RabbitListener(queues = {"fanout.farm.item.queue"})
    @RabbitHandler
    public void receiveMessage(Long itemId) {
        log.info("兑换信息====" + itemId);
        farmService.create(itemId);
    }
}
