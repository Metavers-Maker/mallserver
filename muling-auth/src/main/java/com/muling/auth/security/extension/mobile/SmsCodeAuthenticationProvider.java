package com.muling.auth.security.extension.mobile;

import cn.hutool.core.util.ArrayUtil;
import com.muling.auth.security.core.userdetails.member.MemberUserDetails;
import com.muling.auth.security.core.userdetails.member.MemberUserDetailsServiceImpl;
import com.muling.common.enums.VCodeTypeEnum;
import com.muling.common.exception.BizException;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.common.util.VCodeUtils;
import com.muling.mall.ums.api.MemberFeignClient;
import com.muling.mall.ums.enums.MemberStatusEnum;
import com.muling.mall.ums.pojo.dto.MemberAuthDTO;
import com.muling.mall.ums.pojo.dto.MemberDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.HashSet;

/**
 * 短信验证码认证授权提供者
 */
@Data
@Slf4j
public class SmsCodeAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;
    private MemberFeignClient memberFeignClient;
    private StringRedisTemplate redisTemplate;

    private Environment env;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SmsCodeAuthenticationToken authenticationToken = (SmsCodeAuthenticationToken) authentication;
        String mobile = (String) authenticationToken.getPrincipal();
        String code = (String) authenticationToken.getCredentials();
        boolean isDev = ArrayUtil.contains(env.getActiveProfiles(), "dev");
        boolean jumpVerify = false;
        if (isDev && code.equals("6666")) {
            //开发模式 6666为万能验证码，其它模式正常
            jumpVerify = true;
        }
        if (!jumpVerify) {
            boolean b = VCodeUtils.checkVCode(redisTemplate, VCodeTypeEnum.LOGIN, mobile, code);
            if (!b) {
                throw new BizException(ResultCode.VERIFY_CODE_ERROR);
            }
        }
        //
        Result<MemberAuthDTO> memberAuthResult = memberFeignClient.loadUserByMobile(mobile);
        boolean isNew = false;//new or not
        if (memberAuthResult == null) {
            throw new BizException(ResultCode.USER_LOGIN_ERROR);
        } else if (ResultCode.USER_NOT_EXIST.getCode().equals(memberAuthResult.getCode())) {
            //用户不存在，注册新的会员
            MemberDTO memberDTO = new MemberDTO();
            memberDTO.setMobile(mobile);
            memberFeignClient.addMember(memberDTO);
            isNew = true;
        } else if (ResultCode.SUCCESS.getCode().equals(memberAuthResult.getCode())
                && memberAuthResult.getData().getStatus() == MemberStatusEnum.FORBIDDEN.getValue()) {
            throw new BizException(ResultCode.USER_FORBIDDEN);
        }
        //
        MemberUserDetails userDetails = (MemberUserDetails) ((MemberUserDetailsServiceImpl) userDetailsService).loadUserByMobile(mobile);
        userDetails.setIsNew(isNew);
        SmsCodeAuthenticationToken result = new SmsCodeAuthenticationToken(userDetails, authentication.getCredentials(), new HashSet<>());
        result.setDetails(authentication.getDetails());
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SmsCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
