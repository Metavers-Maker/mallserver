package com.muling.mall.chat.service;

import com.muling.mall.chat.dto.ResponseMessage;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ChatService {

    @Resource
    private SimpMessagingTemplate messagingTemplate;

    public void sendPublicMessage(final String message) {
        ResponseMessage response = new ResponseMessage(message);
        messagingTemplate.convertAndSend("/topic/public-messages", response);
    }

    public void sendPrivateMessage(final String username, final String message, final String sessionId) {
        ResponseMessage response = new ResponseMessage(message);
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create();
        accessor.setHeader(SimpMessageHeaderAccessor.SESSION_ID_HEADER, sessionId);
        messagingTemplate.convertAndSendToUser(username, "/queue/private-messages", response, accessor.getMessageHeaders());
    }
}
