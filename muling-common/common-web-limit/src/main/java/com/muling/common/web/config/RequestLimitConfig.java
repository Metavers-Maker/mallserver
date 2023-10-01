package com.muling.common.web.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 *
 */
@Configuration
@ComponentScan(basePackages = "com.muling.**")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class RequestLimitConfig {
    private Logger logger = LoggerFactory.getLogger(RequestLimitConfig.class);


}
