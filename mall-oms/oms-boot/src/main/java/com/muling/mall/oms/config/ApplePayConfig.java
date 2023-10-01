package com.muling.mall.oms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "apple.pay")
public class ApplePayConfig {

    public static String applePayUrl = "https://sandbox.itunes.apple.com/verifyReceipt";

    /**
     * 正式URL
     */
    public static String OFFICAL_URL = "https://buy.itunes.apple.com/verifyReceipt";

    /**
     * 沙盒URL
     */
    public static String SAND_BOX_URL = "https://sandbox.itunes.apple.com/verifyReceipt";

    public String getApplePayUrl() {
        return applePayUrl;
    }

    @Value("${apple.pay.apple-pay-url}")
    public void setApplePayUrl(String applePayUrl) {
        ApplePayConfig.applePayUrl = applePayUrl;
    }
}
