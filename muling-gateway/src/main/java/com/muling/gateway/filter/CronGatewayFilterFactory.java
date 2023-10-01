package com.muling.gateway.filter;

import cn.hutool.core.util.StrUtil;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.muling.common.result.ResultCode;
import com.muling.gateway.util.WebFluxUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class CronGatewayFilterFactory extends AbstractGatewayFilterFactory<CronGatewayFilterFactory.Config> {

    final CronParser parser;

    public CronGatewayFilterFactory() {
        super(Config.class);
        parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING));
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            //每天9点到18点 "* * 9-18 * * ?"
            String paths = config.getPaths();
            String rawPath = exchange.getRequest().getURI().getRawPath();
            log.info("rawPath:" + rawPath);
            if (StrUtil.isNotBlank(paths) && StrUtil.contains(config.getPaths(), rawPath)) {
                ExecutionTime executionTime = ExecutionTime.forCron(parser.parse(config.getExpression()));
                boolean match = executionTime.isMatch(ZonedDateTime.now());
                log.info("cron [{}] [{}]", config.getExpression(), match);
                if (match) {
                    return chain.filter(exchange);
                } else {
                    return WebFluxUtils.writeResponse(exchange.getResponse(), ResultCode.FORBIDDEN_OPERATION);
                }
            } else {
                return chain.filter(exchange);
            }
        };
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("expression", "paths");
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Config {
        private String expression;
        private String paths;
    }
}
