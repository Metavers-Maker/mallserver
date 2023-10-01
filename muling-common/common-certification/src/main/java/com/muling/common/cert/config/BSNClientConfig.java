package com.muling.common.cert.config;

import lombok.RequiredArgsConstructor;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;


/**
 * @author chen
 */
@Configuration
@RequiredArgsConstructor
public class BSNClientConfig {
    private final BSNConfig bsnConfig;

    @Bean
    public CloseableHttpClient httpClient() throws Exception {
        HttpClientBuilder builder = HttpClientBuilder.create();

        // 1.为HttpClientBuilder设置绕过不安全的https证书
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", getSslConnectionSocketFactory())
                .build();

        // 2.为HttpClientBuilder设置PoolingHttpClientConnectionManager
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        cm.setMaxTotal(bsnConfig.getMaxTotal());
        cm.setDefaultMaxPerRoute(bsnConfig.getDefaultMaxPerRoute());
        builder.setConnectionManager(cm);

        // 3.为HttpClientBuilder设置从连接池获取连接的超时时间、连接超时时间、获取数据响应超时时间
        RequestConfig requestConfig = RequestConfig.custom().
                setConnectionRequestTimeout(bsnConfig.getConnectionRequestTimeout()).
                setConnectTimeout(bsnConfig.getConnectTimeout()).
                setSocketTimeout(bsnConfig.getSocketTimeout()).
                build();

        builder.setDefaultRequestConfig(requestConfig);
        return builder.build();
    }

    /**
     * 支持SSL
     *
     * @return SSLConnectionSocketFactory
     */
    private static SSLConnectionSocketFactory getSslConnectionSocketFactory() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        TrustStrategy acceptingTrustStrategy = (x509Certificates, s) -> true;
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        return new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
    }

}
