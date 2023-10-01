package com.muling.mall.chat.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.muling.mall.chat.entity.User;
import com.muling.common.constant.SecurityConstants;
import com.nimbusds.jose.JWSObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class AuthChannelInterceptor implements ChannelInterceptor {

    /**
     * 连接前监听
     *
     * @param message
     * @param channel
     * @return
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        //1、判断是否首次连接
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            return getMessage(message, accessor);
        }
        //不是首次连接，已经登陆成功
        return message;
    }


    private Message<?> getMessage(Message<?> message, StompHeaderAccessor accessor) {
        //2、判断token
        List<String> nativeHeader = accessor.getNativeHeader(SecurityConstants.AUTHORIZATION_KEY);
        String sessionId = accessor.getSessionId();
        if (nativeHeader != null && !nativeHeader.isEmpty()) {
            String token = nativeHeader.get(0);
            if (StringUtils.isNotBlank(token)) {
                try {
                    token = StrUtil.replaceIgnoreCase(token, SecurityConstants.JWT_PREFIX, StrUtil.EMPTY);
                    String payload = StrUtil.toString(JWSObject.parse(token).getPayload());
                    JSONObject jsonObject = JSONUtil.parseObj(payload);
                    Long userId = jsonObject.getLong(SecurityConstants.USER_ID_KEY);
                    String username = jsonObject.getStr(SecurityConstants.USER_NAME_KEY);

                    log.info("User with ID '{}' '{}' opened the page", userId, username);

                    //如果存在用户信息，将用户名赋值，后期发送时，可以指定用户名即可发送到对应用户
                    User user = new User(sessionId, userId, username);
                    accessor.setUser(user);
                } catch (Exception e) {
                }
                return message;
            }
        }
        return null;
    }

}
