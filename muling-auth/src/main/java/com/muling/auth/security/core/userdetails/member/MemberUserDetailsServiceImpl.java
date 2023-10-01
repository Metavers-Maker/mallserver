package com.muling.auth.security.core.userdetails.member;

import com.muling.mall.ums.api.MemberFeignClient;
import com.muling.mall.ums.pojo.dto.MemberAuthDTO;
import com.muling.common.enums.AuthenticationMethodEnum;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 商城会员用户认证服务
 */

@Service("memberUserDetailsService")
@RequiredArgsConstructor
public class MemberUserDetailsServiceImpl implements UserDetailsService {

    private final MemberFeignClient memberFeignClient;

    /**
     * 用户名认证方式
     * @param username
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        MemberUserDetails userDetails = null;
        Result<MemberAuthDTO> result = memberFeignClient.loadUserByUsername(username);
        if (Result.isSuccess(result)) {
            MemberAuthDTO member = result.getData();
            if (null != member) {
                userDetails = new MemberUserDetails(member);
                userDetails.setAuthenticationMethod(AuthenticationMethodEnum.USERNAME.getValue());   // 认证方式：Username
            }
        }
        return memberCheck(userDetails);
    }

    /**
     * 手机号码认证方式
     * @param mobile
     */
    public UserDetails loadUserByMobile(String mobile) {
        MemberUserDetails userDetails = null;
        Result<MemberAuthDTO> result = memberFeignClient.loadUserByMobile(mobile);
        if (Result.isSuccess(result)) {
            MemberAuthDTO member = result.getData();
            if (null != member) {
                userDetails = new MemberUserDetails(member);
                userDetails.setAuthenticationMethod(AuthenticationMethodEnum.MOBILE.getValue());   // 认证方式：Mobile
            }
        }
        return memberCheck(userDetails);
    }

    /**
     * 邮箱认证方式
     * @param email
     */
    public UserDetails loadUserByEmail(String email) {
        MemberUserDetails userDetails = null;
        Result<MemberAuthDTO> result = memberFeignClient.loadUserByEmail(email);
        if (Result.isSuccess(result)) {
            MemberAuthDTO member = result.getData();
            if (null != member) {
                userDetails = new MemberUserDetails(member);
                userDetails.setAuthenticationMethod(AuthenticationMethodEnum.EMAIL.getValue());   // 认证方式：Email
            }
        }
        return memberCheck(userDetails);
    }

    /**
     * openid 认证方式
     * @param openId
     */
    public UserDetails loadUserByOpenId(String openId) {
        MemberUserDetails userDetails = null;
        Result<MemberAuthDTO> result = memberFeignClient.loadUserByOpenId(openId);
        if (Result.isSuccess(result)) {
            MemberAuthDTO member = result.getData();
            if (null != member) {
                userDetails = new MemberUserDetails(member);
                userDetails.setAuthenticationMethod(AuthenticationMethodEnum.OPENID.getValue());   // 认证方式：OpenId
            }
        }
        return memberCheck(userDetails);
    }

    /**
     * alipayId 认证方式
     * @param alipayId
     */
    public UserDetails loadUserByAlipayId(String alipayId) {
        MemberUserDetails userDetails = null;
        Result<MemberAuthDTO> result = memberFeignClient.loadUserByAlipayId(alipayId);
        if (Result.isSuccess(result)) {
            MemberAuthDTO member = result.getData();
            if (null != member) {
                userDetails = new MemberUserDetails(member);
                userDetails.setAuthenticationMethod(AuthenticationMethodEnum.ALIPAYID.getValue());   // 认证方式：alipayId
            }
        }
        return memberCheck(userDetails);
    }

    public UserDetails memberCheck(UserDetails userDetails) {
        if (userDetails == null) {
            throw new UsernameNotFoundException(ResultCode.USER_NOT_EXIST.getMsg());
        } else if (!userDetails.isEnabled()) {
            throw new DisabledException("the account is  forbidden!");
        } else if (!userDetails.isAccountNonLocked()) {
            throw new LockedException("该账号已被锁定!");
        } else if (!userDetails.isAccountNonExpired()) {
            throw new AccountExpiredException("该账号已过期!");
        }
        return userDetails;
    }
}
