package com.muling.auth.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthEventListener {

    /**
     * 登陆鉴权成功事件处理
     *
     * @param event
     */
    @EventListener
    public void successEvent(AuthenticationSuccessEvent event) {
        log.info("-----------------用户登陆鉴权成功-----------------");
        if (!event.getSource().getClass().getName().equals(
                "org.springframework.security.authentication.UsernamePasswordAuthenticationToken")) {
            return;
        }

        if (event.getAuthentication().getDetails() != null) {
            //todo 插入用户登陆成功日志
        }
    }

    /**
     * 登陆鉴权错误事件处理
     *
     * @param event
     */
    @EventListener
    public void failureBadCredentialsEvent(AuthenticationFailureBadCredentialsEvent event) {
        log.info("-----------------插入用户登陆失败日志-----------------");
        if (event.getAuthentication().getDetails() != null) {
            String username = event.getAuthentication().getPrincipal().toString();
            String errorMessage = event.getException().getMessage();
            //todo 插入用户登陆失败日志
        }
    }
}
