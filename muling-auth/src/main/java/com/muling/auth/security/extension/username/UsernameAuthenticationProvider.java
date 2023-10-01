package com.muling.auth.security.extension.username;

import com.muling.auth.security.core.userdetails.member.MemberUserDetails;
import com.muling.auth.security.core.userdetails.member.MemberUserDetailsServiceImpl;
import com.muling.common.auth.GoogleAuthenticator;
import com.muling.common.constant.GlobalConstants;
import com.muling.common.exception.BizException;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.common.util.MD5Util;
import com.muling.mall.ums.api.MemberFeignClient;
import com.muling.mall.ums.enums.MemberStatusEnum;
import com.muling.mall.ums.pojo.dto.MemberAuthDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.HashSet;

/**
 * 手机号密码认证授权提供者
 *
 * @author <a href="mailto:xianrui0365@163.com">xianrui</a>
 * @date 2021/9/25
 */
@Data
@Slf4j
public class UsernameAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;
    private MemberFeignClient memberFeignClient;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernameAuthenticationToken authenticationToken = (UsernameAuthenticationToken) authentication;
        String username = authenticationToken.getUsername();
        String email = authenticationToken.getEmail();
        String password = authenticationToken.getPassword();
        String googleCode = authenticationToken.getGoogleCode();
        log.info("username:{},email:{}", username, email);
        //
        boolean isUserName = false;
        MemberAuthDTO memberAuthDTO = null;
        if (StringUtils.isNotBlank(username)) {
            isUserName = true;
            Result<MemberAuthDTO> memberAuthResult = memberFeignClient.loadUserByMobile(username);
            log.info("用户名登录 username:{}", username);
            //用户名登录检测
            if (memberAuthResult == null) {
                throw new BizException(ResultCode.USER_LOGIN_ERROR);
            } else if (ResultCode.USER_NOT_EXIST.getCode().equals(memberAuthResult.getCode())) {
                throw new BizException(ResultCode.USER_NOT_EXIST);
            } else if (memberAuthResult.getData().getStatus() == MemberStatusEnum.FORBIDDEN.getValue()) {//GlobalConstants.USER_STATUS_FORBIDDEN
                throw new BizException(ResultCode.USER_FORBIDDEN);
            } else if (!MD5Util.encodeSaltMD5(password, memberAuthResult.getData().getSalt()).equals(memberAuthResult.getData().getPassword())) {
                throw new BizException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
            } else if (memberAuthResult.getData().getStatus() == MemberStatusEnum.UNACTIVATED.getValue()) {
                throw new BizException(ResultCode.USER_NOT_ACTIVAT);
            }
            memberAuthDTO = memberAuthResult.getData();
        }

        //邮箱登录检测
        boolean isEmail = false;
        if (StringUtils.isNotBlank(email) && isUserName == false) {
            isEmail = true;
            Result<MemberAuthDTO> memberAuthResult = memberFeignClient.loadUserByEmail(email);
            log.info("邮箱登录 email:{}", email);
            if (memberAuthResult == null) {
                throw new BizException(ResultCode.USER_LOGIN_ERROR);
            } else if (ResultCode.USER_NOT_EXIST.getCode().equals(memberAuthResult.getCode())) {
                throw new BizException(ResultCode.USER_NOT_EXIST);
            } else if (memberAuthResult.getData().getStatus() == MemberStatusEnum.FORBIDDEN.getValue()) {//GlobalConstants.USER_STATUS_FORBIDDEN
                throw new BizException(ResultCode.USER_FORBIDDEN);
            } else if (!MD5Util.encodeSaltMD5(password, memberAuthResult.getData().getSalt()).equals(memberAuthResult.getData().getPassword())) {
                throw new BizException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
            } else if (memberAuthResult.getData().getStatus() == MemberStatusEnum.UNACTIVATED.getValue()) {
                throw new BizException(ResultCode.USER_NOT_ACTIVAT);
            }
            memberAuthDTO = memberAuthResult.getData();
        }

        //如果绑定google验证，走google验证
        if (memberAuthDTO.getIsBindGoogle() == GlobalConstants.STATUS_YES) {
            /***检验谷歌验证码*/
            long t = System.currentTimeMillis();
            GoogleAuthenticator ga = new GoogleAuthenticator();
            //should give 5 * 30 seconds of grace...
            ga.setWindowSize(5);
            long codeNum = 0;
            try {
                codeNum = Long.parseLong(googleCode);
            } catch (NumberFormatException e) {
                log.error("google login error:{},code:{}", "谷歌验证码错误(不是数字)", googleCode);
                throw new BizException(ResultCode.GOOGLECODE_ERROR);
            }
            boolean r = ga.checkCode(memberAuthDTO.getSecret(), codeNum, t);
            if (!r) {    //如果校验没通过,进行错误处理
                log.error("google login error:{},username:{}", "谷歌验证码错误", memberAuthDTO.getUsername());
                throw new BizException(ResultCode.GOOGLECODE_ERROR);
            }
        }
        if (isUserName) {
            //用户名登录
            log.info("用户名登录成功：{}", username);
            MemberUserDetails userDetails = (MemberUserDetails) ((MemberUserDetailsServiceImpl) userDetailsService).loadUserByMobile(username);
            UsernameAuthenticationToken result = new UsernameAuthenticationToken(userDetails, new HashSet<>());
            result.setDetails(authentication.getDetails());
            return result;
        }
        if(isEmail) {
            //邮箱登录
            log.info("邮箱登录成功：{}", email);
            MemberUserDetails userDetails = (MemberUserDetails) ((MemberUserDetailsServiceImpl) userDetailsService).loadUserByEmail(email);
            UsernameAuthenticationToken result = new UsernameAuthenticationToken(userDetails, new HashSet<>());
            result.setDetails(authentication.getDetails());
            return result;
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernameAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
