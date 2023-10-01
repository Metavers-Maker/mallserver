package com.muling.mall.pms.config;

import com.google.common.base.Charsets;
import com.google.common.hash.Funnel;
import com.muling.mall.pms.component.BloomRedisService;
import com.muling.mall.pms.utils.BloomFilterUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @Author DaniR
 * @Description
 * @Date 2021/6/26 9:39
 **/
@Slf4j
@Configuration
@AllArgsConstructor
public class BloomFilterConfig {

    private final RedisTemplate redisTemplate;

    @Bean
    public BloomFilterUtils<String> initBloomFilterHelper() {
        return new BloomFilterUtils<>((Funnel<String>) (from, into) -> into.putString(from, Charsets.UTF_8)
                .putString(from, Charsets.UTF_8), 1000000, 0.01);
    }


    @Bean
    public BloomRedisService bloomRedisService() {
        BloomRedisService bloomRedisService = new BloomRedisService();
        bloomRedisService.setBloomFilterUtils(initBloomFilterHelper());
        bloomRedisService.setRedisTemplate(redisTemplate);
        return bloomRedisService;
    }

}
