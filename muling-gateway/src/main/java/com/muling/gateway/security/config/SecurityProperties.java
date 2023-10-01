package com.muling.gateway.security.config;

import cn.hutool.core.convert.Convert;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Setter
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    /**
     * 忽略URL，List列表形式
     */
    private List<String> ignoreUrls = new ArrayList<>();

    /**
     * 自定义getter方法，并将ENDPOINTS加入至忽略URL列表
     *
     * @return List
     */
    public String[] getIgnoreUrls() {
        return Convert.toStrArray(ignoreUrls);
    }
}
