package com.muling.auth.security.extension.email;

import cn.hutool.core.util.StrUtil;
import com.muling.auth.security.core.userdetails.member.MemberUserDetailsServiceImpl;
import com.muling.mall.ums.api.MemberFeignClient;
import com.muling.common.constant.SecurityConstants;
import com.muling.common.exception.BizException;
import com.muling.common.result.ResultCode;
import lombok.Data;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.HashSet;

/**
 * 邮箱验证码认证授权提供者
 */
@Data
public class MailCodeAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;
    private MemberFeignClient memberFeignClient;
    private StringRedisTemplate redisTemplate;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        MailCodeAuthenticationToken authenticationToken = (MailCodeAuthenticationToken) authentication;
        String email = (String) authenticationToken.getPrincipal();
        String code = (String) authenticationToken.getCredentials();

        String codeKey = SecurityConstants.EMAIL_CODE_PREFIX + email;
        String correctCode = redisTemplate.opsForValue().get(codeKey);
        // 验证码比对
        if (StrUtil.isBlank(correctCode) || !code.equals(correctCode)) {
            throw new BizException(ResultCode.VERFICATION_INVALID_ERROR);
        }
        // 比对成功删除缓存的验证码
        redisTemplate.delete(codeKey);

        UserDetails userDetails = ((MemberUserDetailsServiceImpl) userDetailsService).loadUserByEmail(email);
        MailCodeAuthenticationToken result = new MailCodeAuthenticationToken(userDetails, authentication.getCredentials(), new HashSet<>());
        result.setDetails(authentication.getDetails());
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return MailCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
