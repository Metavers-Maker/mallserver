package com.muling.mall.oms.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "alipay")
public class AlipayConfig {

    public static String appId;

    public static String privateKey;

    public static String aliPayPublicKey;

    public static String notifyUrl;

    /**
     * 请求支付宝的网关地址
     */
    public static String gatewayUrl;


    /**
     * 返回格式
     */
    public static String format;

    /**
     * 加密类型
     */
    public static String signType;

    /**
     * 编码
     */
    public static String charset;

    public String getCharset() {
        return charset;
    }

    @Value("${alipay.charset}")
    public void setCharset(String charset) {
        AlipayConfig.charset = charset;
    }

    public String getFormat() {
        return format;
    }

    @Value("${alipay.format}")
    public void setFormat(String format) {
        AlipayConfig.format = format;
    }

    public String getSignType() {
        return signType;
    }

    @Value("${alipay.sign-type}")
    public void setSignType(String signType) {
        AlipayConfig.signType = signType;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    @Value("${alipay.private-key}")
    public void setPrivateKey(String privateKey) {
        AlipayConfig.privateKey = privateKey;
    }

    public String getAliPayPublicKey() {
        return aliPayPublicKey;
    }

    @Value("${alipay.alipay-public-key}")
    public void setAliPayPublicKey(String aliPayPublicKey) {
        AlipayConfig.aliPayPublicKey = aliPayPublicKey;
    }

    public String getAppId() {
        return appId;
    }

    @Value("${alipay.app-id}")
    public void setAppId(String appId) {
        AlipayConfig.appId = appId;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    @Value("${alipay.notify-url}")
    public void setNotifyUrl(String notifyUrl) {
        AlipayConfig.notifyUrl = notifyUrl;
    }

    public String getGatewayUrl() {
        return gatewayUrl;
    }

    @Value("${alipay.gateway-url}")
    public void setGatewayUrl(String gatewayUrl) {
        AlipayConfig.gatewayUrl = gatewayUrl;
    }


    @Bean
    public AlipayClient alipayClient() {
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.appId, AlipayConfig.privateKey,
                AlipayConfig.format, AlipayConfig.charset, AlipayConfig.aliPayPublicKey, AlipayConfig.signType);

        return alipayClient;
    }
}
