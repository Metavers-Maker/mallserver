package com.muling.common.elasticsearch.config;

import cn.hutool.core.util.ArrayUtil;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

import java.util.List;

/**
 * ElasticSearch HighLevelClient
 *
 * @author hxr
 * @date 2021-03-05
 */
@ConfigurationProperties(prefix = "spring.elasticsearch.rest")
@Configuration
public class RestHighLevelClientConfig {

    @Setter
    private List<String> clusterNodes;
    @Setter
    private String clusterPassword;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        String[] nodes = ArrayUtil.toArray(clusterNodes, String.class);

        ClientConfiguration.MaybeSecureClientConfigurationBuilder clientConfigurationBuilder = ClientConfiguration.builder()
                .connectedTo(nodes);
        if (StringUtils.isNotBlank(clusterPassword)) {
            String[] split = clusterPassword.split(":");
            clientConfigurationBuilder.withBasicAuth(split[0], split[1]);
        }
        ClientConfiguration clientConfiguration = clientConfigurationBuilder.build();

        return RestClients.create(clientConfiguration).rest();
    }

}
