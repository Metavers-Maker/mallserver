package com.muling.common.cert.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "aliyun.cert")
@Configuration
@Data
public class CertConfig {
    private String appKey;
    private String appSecret;
}
