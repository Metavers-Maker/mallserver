package com.muling.common.web.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.muling.common.constant.SecurityConstants;
import com.muling.common.enums.AuthenticationMethodEnum;
import com.nimbusds.jose.JWSObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Map;

/**
 * 请求工具类
 *
 * @author xianrui
 */
@Slf4j
public class RequestUtils {
    @SneakyThrows
    public static String getGrantType() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String grantType = request.getParameter(SecurityConstants.GRANT_TYPE_KEY);
        return grantType;
    }

    @SneakyThrows
    public static String getIp() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return IPUtils.getIpAddr(request);
    }

    @SneakyThrows
    public static String getDeviceId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String deviceId = request.getHeader(SecurityConstants.DEVICE_ID_KEY);
        return deviceId;
    }

    @SneakyThrows
    public static String getUserAgent() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String userAgent = request.getHeader(SecurityConstants.USER_AGENT_KEY);
        return userAgent;
    }

    @SneakyThrows
    public static String getDeviceName() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String deviceName = request.getHeader(SecurityConstants.DEVICE_NAME_KEY);
        return deviceName;
    }


    @SneakyThrows
    public static String getPlatform() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String platform = request.getParameter(SecurityConstants.PLATFORM_KEY);
        return platform;
    }

    @SneakyThrows
    public static Map<String, String> headers() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, String> headers = new HashedMap();

        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                String values = request.getHeader(name);
                //将请求头保存到模板中
                headers.put(name, values);
            }
        }
        return headers;
    }

    /**
     * 获取登录认证的客户端ID
     * <p>
     * 兼容两种方式获取OAuth2客户端信息（client_id、client_secret）
     * 方式一：client_id、client_secret放在请求路径中
     * 方式二：放在请求头（Request Headers）中的Authorization字段，且经过加密，例如 Basic Y2xpZW50OnNlY3JldA== 明文等于 client:secret
     *
     * @return
     */
    @SneakyThrows
    public static String getOAuth2ClientId() {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        // 从请求路径中获取
        String clientId = request.getParameter(SecurityConstants.CLIENT_ID_KEY);
        if (StrUtil.isNotBlank(clientId)) {
            return clientId;
        }

        // 从请求头获取
        String basic = request.getHeader(SecurityConstants.AUTHORIZATION_KEY);
        if (StrUtil.isNotBlank(basic) && basic.startsWith(SecurityConstants.BASIC_PREFIX)) {
            basic = basic.replace(SecurityConstants.BASIC_PREFIX, Strings.EMPTY);
            String basicPlainText = new String(Base64.getDecoder().decode(basic.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
            clientId = basicPlainText.split(":")[0]; //client:secret
        }
        return clientId;
    }

    public static void main(String[] args) {
        String basic = "YmFkbS13ZWI6MTIzNDU2";
        basic = basic.replace(SecurityConstants.BASIC_PREFIX, Strings.EMPTY);
        String basicPlainText = new String(Base64.getDecoder().decode(basic.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        String clientId = basicPlainText.split(":")[0]; //client:secret
        System.out.println(clientId);

        System.out.println(new String(Base64.getEncoder().encode("badm-web:123456".getBytes(StandardCharsets.UTF_8))));
    }

    /**
     * 解析JWT获取获取认证方式
     *
     * @return
     */
    @SneakyThrows
    public static String getAuthenticationMethod() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String refreshToken = request.getParameter(SecurityConstants.REFRESH_TOKEN_KEY);

        String payload = StrUtil.toString(JWSObject.parse(refreshToken).getPayload());
        JSONObject jsonObject = JSONUtil.parseObj(payload);

        String authenticationMethod = jsonObject.getStr(SecurityConstants.AUTHENTICATION_METHOD);
        if (StrUtil.isBlank(authenticationMethod)) {
            authenticationMethod = AuthenticationMethodEnum.USERNAME.getValue();
        }
        return authenticationMethod;
    }
}
