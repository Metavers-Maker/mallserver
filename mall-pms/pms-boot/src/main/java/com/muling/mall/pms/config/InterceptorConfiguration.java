package com.muling.mall.pms.config;

import com.muling.mall.pms.interceptor.BloomFilterInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author DaniR
 * @Description
 * @Date 2021/6/26 9:39
 **/
@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册拦截器
        registry.addInterceptor(bloomInterceptorHandler())
                .addPathPatterns("/app-api/v1/spu/**")
                .excludePathPatterns("/app-api/v1/spu/sku/**");
    }

    @Bean
    public BloomFilterInterceptor bloomInterceptorHandler() {
        return new BloomFilterInterceptor();
    }
}
