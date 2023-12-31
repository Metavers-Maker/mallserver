package com.muling.auth.security.extension.google;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *  google授权者
 *
 * @author hank
 * @date 2021/11/27
 */
public class GoogleTokenGranter extends AbstractTokenGranter {

    /**
     * 声明授权者 CaptchaTokenGranter 支持授权模式 wechat
     * 根据接口传值 grant_type = wechat 的值匹配到此授权者
     * 匹配逻辑详见下面的两个方法
     *
     * @see CompositeTokenGranter#grant(String, TokenRequest)
     * @see AbstractTokenGranter#grant(String, TokenRequest)
     */
    private static final String GRANT_TYPE = "google";
    private final AuthenticationManager authenticationManager;

    public GoogleTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, AuthenticationManager authenticationManager) {
        super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {

        Map<String, String> parameters = new LinkedHashMap(tokenRequest.getRequestParameters());
        String idToken = parameters.get("idToken");
        String googleCode = parameters.get("googleCode");
        String inviteCode = parameters.get("code");
        // 过河拆桥，移除后续无用参数
        parameters.remove("idToken");
        parameters.remove("googleCode");
        //创建Google认证流程
        Authentication userAuth = new GoogleAuthenticationToken(null, idToken,googleCode,inviteCode); // 未认证状态
        ((AbstractAuthenticationToken) userAuth).setDetails(parameters);
        try {
            // 登录认中
            userAuth = this.authenticationManager.authenticate(userAuth);
        } catch (Exception e) {
            throw new InvalidGrantException(e.getMessage());
        }

        if (userAuth != null && userAuth.isAuthenticated()) {
            // 登录认证成功，获取Token
            OAuth2Request storedOAuth2Request = this.getRequestFactory().createOAuth2Request(client, tokenRequest);
            return new OAuth2Authentication(storedOAuth2Request, userAuth);
        } else { // 认证失败
            throw new InvalidGrantException("Could not authenticate idToken: " + idToken);
        }
    }
}
