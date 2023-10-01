package com.muling.common.redis.config;

import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;

/**
 * Redis缓存配置
 */
@EnableConfigurationProperties(CacheProperties.class)
@Configuration
@EnableCaching
public class RedisCacheConfig {

    /**
     * RedisCacheConfiguration Bean
     * <p>
     * 参考 org.springframework.boot.autoconfigure.cache.RedisCacheConfiguration 的 createConfiguration 方法
     */
    @Bean
    @Primary
    public RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties) {
        // 设置使用 JSON 序列化方式
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        config = config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json()));

        // 设置 CacheProperties.Redis 的属性
        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            config = config.prefixCacheNameWith(redisProperties.getKeyPrefix());
        }
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }
        return config;
    }

//    @Bean
//    public CacheManager getCacheManager(RedisTemplate<String, Object> template) {
//        //基本配置
//        RedisCacheManager redisCacheManager = RedisCacheManager.RedisCacheManagerBuilder
//                // Redis 连接工厂
//                .fromConnectionFactory(template.getConnectionFactory())
//                .cacheDefaults(getCacheConfigurationWithTtl(template, Duration.ZERO))
//                .withCacheConfiguration("cache_card", getCacheConfigurationWithTtl(template, Duration.ofHours(2)))
//                .withCacheConfiguration("cache_ums", getCacheConfigurationWithTtl(template, Duration.ofHours(2)))
//                // 配置同步修改或删除 put/evict
//                .transactionAware()
//                .build();
//        return redisCacheManager;
//    }
//
//    RedisCacheConfiguration getCacheConfigurationWithTtl(RedisTemplate<String, Object> template, Duration duration) {
//
//        return RedisCacheConfiguration
//                .defaultCacheConfig()
//                // 设置key为String
//                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(template.getStringSerializer()))
//                // 设置value 为自动转Json的Object
//                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(template.getValueSerializer()))
//                // 不缓存null
//                .disableCachingNullValues()
//                // 缓存数据保存1小时
//                .entryTtl(duration);
//    }
}
