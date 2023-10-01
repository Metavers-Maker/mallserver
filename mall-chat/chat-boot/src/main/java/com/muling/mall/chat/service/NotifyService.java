package com.muling.mall.chat.service;

import com.muling.mall.chat.dto.ResponseMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class NotifyService {

    @Resource
    private SimpMessagingTemplate messagingTemplate;

    public void sendPublicNotify() {
        ResponseMessage message = new ResponseMessage("Public notification");
        messagingTemplate.convertAndSend("/topic/public-notify", message);
    }

    public void sendPrivateNotify(final String username) {
        ResponseMessage message = new ResponseMessage("Private Notify");
        messagingTemplate.convertAndSendToUser(username, "/queue/private-notify", message);
    }
}
