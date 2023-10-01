package com.muling.mall.ums.test;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class FanoutConfig {

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange("fanout_exchange_order", true, false);
    }

    @Bean
    public Queue emailQueue() {
        return new Queue("fanout.email.queue", true);
    }

    @Bean
    public Queue weChatQueue() {
        return new Queue("fanout.wechat.queue", true);
    }

    @Bean
    public Binding emailBinding() {
        return BindingBuilder.bind(emailQueue()).to(fanoutExchange());
    }

    @Bean
    public Binding weChatBinding() {
        return BindingBuilder.bind(weChatQueue()).to(fanoutExchange());
    }

}
