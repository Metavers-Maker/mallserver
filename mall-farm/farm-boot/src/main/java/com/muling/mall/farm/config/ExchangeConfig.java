package com.muling.mall.farm.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ExchangeConfig {

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange("fanout.item.exchange", true, false);
    }

    @Bean
    public Queue farmQueue() {
        return new Queue("fanout.farm.item.queue", true);
    }

    @Bean
    public Binding farmBinding() {
        return BindingBuilder.bind(farmQueue()).to(fanoutExchange());
    }

}
