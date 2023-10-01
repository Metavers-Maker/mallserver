package com.muling.mall.chat.listener;

import com.muling.mall.chat.dto.ChatMessage;
import com.muling.mall.chat.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    @EventListener(classes = {BrokerAvailabilityEvent.class})
    public void brokerAvailabilityEvent(BrokerAvailabilityEvent event) {
        System.out.println("监听到事件:" + event);
    }


    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    /**
     * 连接成功事件
     *
     * @param event
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        User token = (User) headerAccessor.getHeader("simpUser");
        if (token.getName() != null) {
            messagingTemplate.convertAndSend("/topic/public-message", new ChatMessage(ChatMessage.MessageType.JOIN, "上线了", token.getName()));
        }
    }

    /**
     * 断开连接事件
     *
     * @param event
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        User token = (User) headerAccessor.getHeader("simpUser");
        if (token.getName() != null) {
            messagingTemplate.convertAndSend("/topic/public-message", new ChatMessage(ChatMessage.MessageType.LEAVE, "下线了", token.getName()));
        }
    }
}
