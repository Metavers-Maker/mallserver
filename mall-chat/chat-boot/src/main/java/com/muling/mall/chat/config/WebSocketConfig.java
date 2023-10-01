package com.muling.mall.chat.config;

import com.muling.mall.chat.decorator.AuthWebSocketHandlerDecoratorFactory;
import com.muling.mall.chat.handler.CustomHandshakeHandler;
import com.muling.mall.chat.interceptor.AuthChannelInterceptor;
import com.muling.mall.chat.interceptor.SessionAuthHandshakeInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import javax.annotation.Resource;

/**
 * 使用此注解来标识使能WebSocket的broker.即使用broker来处理消息.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Resource
    private AuthChannelInterceptor authChannelInterceptor;

    @Resource
    private AuthWebSocketHandlerDecoratorFactory authWebSocketHandlerDecoratorFactory;

    @Resource
    private CustomHandshakeHandler customHandshakeHandler;

    @Resource
    private SessionAuthHandshakeInterceptor sessionAuthHandshakeInterceptor;

    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.amqp-port}")
    private Integer port;
    @Value("${spring.rabbitmq.username}")
    private String usename;
    @Value("${spring.rabbitmq.password}")
    private String password;

    @Override
    public void configureMessageBroker(final MessageBrokerRegistry registry) {
//        registry.setPathMatcher(new AntPathMatcher("."));
        //启用enableStompBrokerRelay，使得订阅到此"topic","queue"前缀的客户端可以收到greeting消息.
        registry.enableStompBrokerRelay("/topic", "/queue")
                .setRelayHost(host)
                .setRelayPort(port)
                // 配置发送消息到stomp代理的系统共享连接的账号密码，默认是guest/guest
                .setSystemLogin(usename)
                .setSystemPasscode(password);
        //将"app"前缀绑定到MessageMapping注解指定的方法上。如"ws/hello"被指定用greeting()方法来处理.
        registry.setApplicationDestinationPrefixes("/ws");
        //指定用户发送（一对一）的主题前缀是"/user/"
        registry.setUserDestinationPrefix("/user");
//        客户端是否必须按发布顺序接收消息。
//        registry.setPreservePublishOrder(true);
    }

    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        //用来注册Endpoint，“/websocket”即为客户端尝试建立连接的地址。
        registry.addEndpoint("/websocket")
                .setAllowedOriginPatterns("*")
//                .addInterceptors(sessionAuthHandshakeInterceptor)
//                .setHandshakeHandler(customHandshakeHandler);
                .withSockJS();
    }

    /**
     * 这时实际spring weboscket集群的新增的配置，用于获取建立websocket时获取对应的sessionid值
     *
     * @param registration
     */
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.addDecoratorFactory(authWebSocketHandlerDecoratorFactory);
        registration.setSendTimeLimit(15 * 1000);
        registration.setSendBufferSizeLimit(512 * 1024);
    }

    /**
     * 拦截器方式
     *
     * @param registration
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authChannelInterceptor);
    }
}
