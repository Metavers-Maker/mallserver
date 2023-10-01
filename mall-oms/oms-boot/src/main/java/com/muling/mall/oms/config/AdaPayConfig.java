package com.muling.mall.oms.config;

import com.huifu.adapay.Adapay;
import com.huifu.adapay.model.MerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ada-pay")
public class AdaPayConfig {

    public static String appId;

    public static String apiKey;
    public static String mockApiKey;

    public static String rsaPrivateKey;

    public static String notifyUrl;

    @Value("${ada-pay.app-id}")
    public void setAppId(String appId) {
        AdaPayConfig.appId = appId;
    }

    @Value("${ada-pay.api-key}")
    public void setApiKey(String apiKey) {
        AdaPayConfig.apiKey = apiKey;
    }

    @Value("${ada-pay.mock-api-key}")
    public void setMockApiKey(String mockApiKey) {
        AdaPayConfig.mockApiKey = mockApiKey;
    }

    @Value("${ada-pay.rsa-private-key}")
    public void setRsaPrivateKey(String rsaPrivateKey) {
        AdaPayConfig.rsaPrivateKey = rsaPrivateKey;
    }

    @Value("${ada-pay.notify-url}")
    public void setNotifyUrl(String notifyUrl) {
        AdaPayConfig.notifyUrl = notifyUrl;
    }

    @Bean
    public void adaPayClient() throws Exception {
        MerConfig merConfig = new MerConfig();
        merConfig.setApiKey(apiKey);
        merConfig.setApiMockKey(mockApiKey);
        merConfig.setRSAPrivateKey(rsaPrivateKey);
//        Adapay.debug = true;
        Adapay.initWithMerConfig(merConfig);

    }
}
