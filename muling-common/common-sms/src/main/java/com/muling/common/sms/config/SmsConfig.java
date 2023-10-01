package com.muling.common.sms.config;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "sms")
@Configuration
@Data
public class SmsConfig {

    private String type;

    private AliYun aliYun;

    private AllNet allNet;

    private YunXin yunXin;

    @Data
    public static class AliYun {
        private String accessKeyId;

        private String accessKeySecret;

        private String domain;

        private String regionId;

        private String templateCode;

        private String signName;

    }


    @Data
    public static class AllNet {
        private String url;

        private String userId;

        private String apiKey;

    }

    @Data
    public static class YunXin {
        private String url;

        private String appSecret;

        private String appKey;

    }

    @Bean("smsAcsClient")
    public IAcsClient smsAcsClient() {
        DefaultProfile profile = DefaultProfile.getProfile(aliYun.regionId, aliYun.accessKeyId, aliYun.accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        return client;
    }

}
