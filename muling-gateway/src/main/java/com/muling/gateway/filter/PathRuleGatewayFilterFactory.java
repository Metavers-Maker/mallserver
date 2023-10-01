package com.muling.gateway.filter;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.muling.common.constant.SecurityConstants;
import com.muling.common.result.ResultCode;
import com.muling.common.util.DateUtils;
import com.muling.gateway.util.WebFluxUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class PathRuleGatewayFilterFactory implements GlobalFilter, Ordered {

    private final RedisTemplate redisTemplate;

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String rawPath = exchange.getRequest().getURI().getRawPath();

        Boolean hasKey = redisTemplate.hasKey(SecurityConstants.PATH_PREM_RULE_PREFIX + rawPath);
        if (hasKey) {
            String data = (String) redisTemplate.opsForValue().get(SecurityConstants.PATH_PREM_RULE_PREFIX + rawPath);
            log.debug("rawPath:{} , rule:{}", rawPath, data);
            Rule rule = JSONUtil.toBean(data, Rule.class);
            if (rule.getType() == 0 && rule.getValue().equals("0")) {
                return WebFluxUtils.writeResponse(exchange.getResponse(), ResultCode.FORBIDDEN_OPERATION);
            }
            if (rule.getType() == 1) {
                String[] between = rule.getValue().split("_");
                boolean in = DateUtil.isIn(DateUtil.date(), DateUtil.parse(between[0]), DateUtil.parse(between[1]));
                if (!in) {
                    return WebFluxUtils.writeResponse(exchange.getResponse(), ResultCode.FORBIDDEN_OPERATION);
                }
            }
            if (rule.getType() == 2) {
                String[] between = rule.getValue().split("_");

                SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");//设置日期格式
                Date nowTime = df.parse(df.format(new Date()));
                //规定时间段
                Date beginTime = df.parse(between[0]);
                Date endTime = df.parse(between[1]);

                boolean in = DateUtils.isIn(nowTime, beginTime, endTime);
                if (!in) {
                    return WebFluxUtils.writeResponse(exchange.getResponse(), ResultCode.FORBIDDEN_OPERATION);
                }
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 1;
    }

    @Data
    public static class Rule {
        private Integer type;
        private String value;
    }
}
