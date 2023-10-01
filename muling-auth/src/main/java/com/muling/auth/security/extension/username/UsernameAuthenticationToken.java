package com.muling.auth.security.extension.username;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;

/**
 * 用户名/邮箱-密码登录
 *
 * @author <a href="mailto:xianrui0365@163.com">xianrui</a>
 * @date 2021/10/5
 */
public class UsernameAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 550L;
    private final Object principal;
    @Getter
    private String username;
    @Getter
    private String email;
    @Getter
    private String password;
    @Getter
    private String googleCode;

    public UsernameAuthenticationToken(Object principal, String username, String email, String password, String googleCode) {
        super((Collection) null);
        this.principal = principal;
        this.username = username;
        this.email = email;
        this.password = password;
        this.googleCode = googleCode;
        this.setAuthenticated(false);
    }

    public UsernameAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    public Object getPrincipal() {
        return this.principal;
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        Assert.isTrue(!isAuthenticated, "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        super.setAuthenticated(false);
    }

    public void eraseCredentials() {
        super.eraseCredentials();
        this.username = null;
        this.email = null;
        this.password = null;
    }
}
