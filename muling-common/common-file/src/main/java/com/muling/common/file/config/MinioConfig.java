package com.muling.common.file.config;

import lombok.Data;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * minio:
 *  endpoint: http://a.muling.tech:9000/
 *  access-key: minioadmin
 *  secret-key: minioadmin
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    /**
     * MinIO的API地址
     */
    @Setter
    private String endpoint;

    /**
     * 用户名
     */
    @Setter
    private String accessKey;

    /**
     * 密钥
     */
    @Setter
    private String secretKey;

    /**
     * 自定义域名(非必须)
     */
    @Setter
    private String customDomain;

    /**
     * 存储桶名称，默认微服务单独一个存储桶
     */
    @Setter
    private String defaultBucket;

    /**
     * 是否开启图片压缩(true:开启;false:关闭)
     */
    @Value("${minio.img_compression_enabled:false}")
    private boolean imgCompressionEnabled;
}
