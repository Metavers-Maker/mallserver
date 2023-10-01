package com.muling.common.redis.config;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@EnableConfigurationProperties(RedissonProperties.class)
@Configuration
public class RedissonConfig {

    private final RedissonProperties redissonProperties;

    @Autowired
    public RedissonConfig(RedissonProperties redissonProperties) {
        this.redissonProperties = redissonProperties;
    }

    @Bean(name = "redissonClient", destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();
        //sentinel
        if (redissonProperties.getSentinel() != null) {
            SentinelServersConfig sentinelServersConfig = config.useSentinelServers();
            sentinelServersConfig.setMasterName(redissonProperties.getSentinel().getMaster());
            sentinelServersConfig.addSentinelAddress(redissonProperties.getSentinel().getNodes().toArray(new String[]{}));
            sentinelServersConfig.setDatabase(redissonProperties.getDatabase());
            if (redissonProperties.getPassword() != null) {
                sentinelServersConfig.setPassword(redissonProperties.getPassword());
            }
            return Redisson.create(config);
        }
        // cluster
        if (redissonProperties.getCluster() != null) {
            ClusterServersConfig clusterServersConfig = config.useClusterServers();
            if (redissonProperties.getPassword() != null) {
                clusterServersConfig.setPassword(redissonProperties.getPassword());
            }
            if (redissonProperties.getUsername() != null) {
                clusterServersConfig.setUsername(redissonProperties.getUsername());
            }
            List<String> nodes = redissonProperties.getCluster().getNodes();
            for (String address : nodes) {
                clusterServersConfig.addNodeAddress(address);
            }
            return Redisson.create(config);
        }
        // single redis
        SingleServerConfig singleServerConfig = config.useSingleServer();
        String schema = redissonProperties.isSsl() ? "rediss://" : "redis://";
        singleServerConfig.setAddress(schema + redissonProperties.getHost() + ":" + redissonProperties.getPort());
        singleServerConfig.setDatabase(redissonProperties.getDatabase());
        if (StringUtils.isNotBlank(redissonProperties.getPassword())) {
            singleServerConfig.setPassword(redissonProperties.getPassword());
        }
        return Redisson.create(config);
    }

}
