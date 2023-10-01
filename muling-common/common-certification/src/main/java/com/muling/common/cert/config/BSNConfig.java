package com.muling.common.cert.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 需要在nacos上配置bsn-avatar相关的信息
 */

@Configuration
public class BSNConfig {

//    @Value("${bsn.host:https://stage.apis.avata.bianjie.ai}")
    @Value("${bsn.host:https://apis.avata.bianjie.ai}")
    private String host;

    @Value("${bsn.api-key:A2l3r065o256a069b2L8E1s2T801r9bz}")
    private String apiKey;

    @Value("${bsn.api-sc:H283I015Q2X6C0x9L2Z8g1n2B801c9mF}")
    private String apiSc;

    @Value("${bsn.account:iaa1lavkjqp23hkrrkv38s6cu89w0f09ahcu7ku8gh}")
    private String account;

    @Value("${bsn.max-total:500}")
    private Integer maxTotal;

    @Value("${bsn.default-max-per-route:50}")
    private Integer defaultMaxPerRoute;

    @Value("${bsn.connection-request-timeout:5000}")
    private Integer connectionRequestTimeout;

    @Value("${bsn.connect-timeout:5000}")
    private Integer connectTimeout;

    @Value("${bsn.socket-timeout:5000}")
    private Integer socketTimeout;

    public String getHost() {
        return host;
    }

    public BSNConfig setHost(String host) {
        this.host = host;
        return this;
    }

    public String getApiKey() {
        return apiKey;
    }

    public BSNConfig setApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public String getAccount() {
        return account;
    }

    public BSNConfig setAccount(String account) {
        this.account = account;
        return this;
    }

    public String getApiSc() {
        return apiSc;
    }

    public BSNConfig setApiSc(String apiSc) {
        this.apiSc = apiSc;
        return this;
    }

    public Integer getMaxTotal() {
        return maxTotal;
    }

    public BSNConfig setMaxTotal(Integer maxTotal) {
        this.maxTotal = maxTotal;
        return this;
    }

    public Integer getDefaultMaxPerRoute() {
        return defaultMaxPerRoute;
    }

    public BSNConfig setDefaultMaxPerRoute(Integer defaultMaxPerRoute) {
        this.defaultMaxPerRoute = defaultMaxPerRoute;
        return this;
    }

    public Integer getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    public BSNConfig setConnectionRequestTimeout(Integer connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
        return this;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public BSNConfig setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public Integer getSocketTimeout() {
        return socketTimeout;
    }

    public BSNConfig setSocketTimeout(Integer socketTimeout) {
        this.socketTimeout = socketTimeout;
        return this;
    }

}
