package com.muling.auth.security.core.userdetails.member;

import com.muling.mall.ums.enums.MemberStatusEnum;
import com.muling.mall.ums.pojo.dto.MemberAuthDTO;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;


/**
 * 用户认证信息
 *
 */

@Data
public class MemberUserDetails implements UserDetails {

    /**
     * 会员Id
     */
    private Long memberId;
    /**
     * 微信开放Id
     */
    private String openId;

    /**
     * 支付宝开放Id
     */
    private String alipayId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 是否新用户
     */
    private Boolean isNew = Boolean.FALSE;

    /**
     * 是否开启
     */
    private Boolean enabled;

    /**
     * 认证方式
     */
    private String authenticationMethod;


    /**
     * 小程序会员用户体系
     */
    public MemberUserDetails(MemberAuthDTO member) {
        this.setMemberId(member.getMemberId());
        this.setOpenId(member.getOpenId());
        this.setAlipayId(member.getAlipayId());
        this.setUsername(member.getUsername());
        this.setEnabled(MemberStatusEnum.COMMON.getValue().equals(member.getStatus()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new HashSet<>();
        return collection;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
