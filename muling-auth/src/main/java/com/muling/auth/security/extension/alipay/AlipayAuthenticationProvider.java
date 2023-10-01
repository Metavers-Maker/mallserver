package com.muling.auth.security.extension.alipay;

import cn.hutool.core.bean.BeanUtil;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserInfoShareRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.muling.auth.security.core.userdetails.member.MemberUserDetailsServiceImpl;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.mall.ums.api.MemberFeignClient;
import com.muling.mall.ums.pojo.dto.MemberAuthDTO;
import com.muling.mall.ums.pojo.dto.MemberDTO;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.HashSet;

/**
 * 支付宝认证提供者
 *
 * @author freeze
 * @date 2022/9/25
 */
@Data
public class AlipayAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;
    private AlipayClient alipayClient;
    private MemberFeignClient memberFeignClient;
    private StringRedisTemplate redisTemplate;
    private Environment env;

    /**
     * 支付宝认证
     *
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    @SneakyThrows
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        AlipayAuthenticationToken authenticationToken = (AlipayAuthenticationToken) authentication;
        String code = (String) authenticationToken.getPrincipal();

        //获取Token
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setGrantType("authorization_code");
        request.setCode("4b203fe6c11548bcabd8da5bb087a83b");
        request.setRefreshToken("201208134b203fe6c11548bcabd8da5bb087a83b");
        AlipaySystemOauthTokenResponse response = alipayClient.execute(request);
        if(response.isSuccess()){
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
        }
        String aliUserId = response.getUserId();

        // 支付宝用户不存在，注册成为新会员
        Result<MemberAuthDTO> memberAuthResult = memberFeignClient.loadUserByAlipayId(aliUserId);
        if (memberAuthResult != null && ResultCode.USER_NOT_EXIST.getCode().equals(memberAuthResult.getCode())) {
//            String sessionKey = sessionInfo.getSessionKey();
//            String encryptedData = authenticationToken.getEncryptedData();
//            String iv = authenticationToken.getIv();
            AlipayUserInfoShareRequest requestInfo = new AlipayUserInfoShareRequest();
            AlipayUserInfoShareResponse responseInfo = alipayClient.execute(requestInfo,response.getAccessToken());
            if(responseInfo.isSuccess()){
                System.out.println("调用成功");
            } else {
                System.out.println("调用失败");
            }
            AlipayUserInfo userInfo = new AlipayUserInfo();

            MemberDTO memberDTO = new MemberDTO();
            BeanUtil.copyProperties(userInfo, memberDTO);
            memberDTO.setAlipayId(aliUserId);
            memberFeignClient.addMember(memberDTO);
        }
        //
        UserDetails userDetails = ((MemberUserDetailsServiceImpl) userDetailsService).loadUserByAlipayId(aliUserId);
        AlipayAuthenticationToken result = new AlipayAuthenticationToken(userDetails, new HashSet<>());
        result.setDetails(authentication.getDetails());
        return result;
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return AlipayAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
