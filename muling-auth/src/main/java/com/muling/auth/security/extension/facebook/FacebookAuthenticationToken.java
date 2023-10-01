package com.muling.auth.security.extension.facebook;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;

/**
 *
 */
public class FacebookAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 550L;
    private final Object principal;
    @Getter
    private String userID;
    @Getter
    private String accessToken;
    @Getter
    private String googleCode;
    @Getter
    private String code;
    /**
     * 账号校验之前的token构建
     *
     * @param principal
     */
    public FacebookAuthenticationToken(Object principal, String userID,String accessToken,String googleCode,String code) {
        super(null);
        this.principal = principal;
        this.accessToken = accessToken;
        this.userID = userID;
        this.googleCode = googleCode;
        this.code = code;
        setAuthenticated(false);
    }

    /**
     * 账号校验成功之后的token构建
     *
     * @param principal
     * @param authorities
     */
    public FacebookAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        Assert.isTrue(isAuthenticated == false, "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        super.setAuthenticated(false);
    }

    public void eraseCredentials() {
        super.eraseCredentials();
    }
}
