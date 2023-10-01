package com.muling.common.logging;

import io.jaegertracing.internal.JaegerTracer;
import io.opentracing.contrib.java.spring.jaeger.starter.JaegerAutoConfiguration;
import io.opentracing.contrib.java.spring.jaeger.starter.JaegerConfigurationProperties;
import io.opentracing.contrib.java.spring.jaeger.starter.TracerBuilderCustomizer;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:taylor.tian@ericsson.com">Taylor Tian</a>
 */
@Configuration
@ConditionalOnClass({JaegerTracer.class, MDC.class})
@ConditionalOnProperty(
        value = {"opentracing.jaeger.log.slf4j.enabled"},
        havingValue = "true",
        matchIfMissing = true
)
@AutoConfigureAfter({JaegerAutoConfiguration.class})
@EnableConfigurationProperties(JaegerConfigurationProperties.class)
public class Slf4jJaegerAutoConfiguration {
    @Bean
    public TracerBuilderCustomizer logTracerCustomizer(JaegerConfigurationProperties properties) {
        TracerBuilderCustomizer customizer = new Slf4jTracerBuilderCustomizer(properties);

        return customizer;
    }
}
