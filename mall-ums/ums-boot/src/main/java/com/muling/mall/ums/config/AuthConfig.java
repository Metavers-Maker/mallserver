package com.muling.mall.ums.config;


import com.muling.mall.ums.util.DesEncrypter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthConfig {

    @Value("${auth.apiKey:da6d86020c4f6d92bee0acf8751be625}")
    public String apiKey;
    @Value("${auth.url:https://testapi.goodsdatas.com:8880/credit/Mobile3EleSim/}")
    public String url;
    @Value("${auth.appCode:4de9e344b6ba40b78992da7aca353f53}")
    public String appCode;
    @Value("${auth.appKey:204102918}")
    public String appKey;
    @Value("${auth.appSecret:NgvHv1zZDzKzS0jCe5SsNAqN45pVKMpp}")
    public String appSecret;
    @Value("${auth.host:https://zpc.market.alicloudapi.com/efficient/cellphone/post}")
    public String host;

    @Bean
    @SneakyThrows
    public DesEncrypter desEncrypter() {
        return new DesEncrypter(apiKey);
    }
}
