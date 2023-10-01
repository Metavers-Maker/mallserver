package com.muling.common.web.config;

import com.muling.common.web.filter.AccessFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccessLogConfig {

    public static final int ACCESS_LOG_FILTER_ORDER = 3;

    @Value("${access.log.pretty:false}")
    private Boolean pretty;

    @Value("${access.log.exclude:/favicon.ico;/monitor/metrics}")
    private String exclude;

    @Value("${access.log.exclude.body.part:#{null}}}")
    private String excludeBodyPart;

    @Value("${access.log.accessSlowMetric.timeWindowSeconds:60}")
    private Integer metricTimeWindowSeconds;

    @Value("${access.log.accessSlowMetric.latencyThreshold:5000}")
    private Integer metricLatencyThreshold;

    @Value("${access.log.accessSlowMetric.uriPatterns:/**}")
    private String metricUriPatterns;

    @Value("${access.api_call.exclude:}")
    private String excludeApiCall;

    @Bean
    public FilterRegistrationBean accessLogFilter() {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setOrder(ACCESS_LOG_FILTER_ORDER);
        bean.setFilter(new AccessFilter());
        bean.setName(AccessFilter.class.getName());
        bean.addUrlPatterns("/*");
        bean.addInitParameter("pretty", pretty.toString());
        bean.addInitParameter("exclude", exclude);
        bean.addInitParameter("excludeBodyPart", excludeBodyPart);
        bean.addInitParameter("excludeApiCall", excludeApiCall);

        return bean;
    }
}
