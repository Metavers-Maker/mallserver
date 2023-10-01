package com.muling.auth.security.extension.wxopen;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONObject;
import com.muling.auth.security.core.userdetails.member.MemberUserDetails;
import com.muling.auth.security.core.userdetails.member.MemberUserDetailsServiceImpl;
import com.muling.auth.security.extension.mobile.SmsCodeAuthenticationToken;
import com.muling.common.cert.service.HttpApiClientWechat;
import com.muling.common.enums.VCodeTypeEnum;
import com.muling.common.exception.BizException;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.common.util.VCodeUtils;
import com.muling.mall.ums.api.MemberFeignClient;
import com.muling.mall.ums.pojo.dto.BindWxopenDTO;
import com.muling.mall.ums.pojo.dto.MemberAuthDTO;
import com.muling.mall.ums.pojo.dto.MemberDTO;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

/**
 * 微信开放平台认证提供者
 */
@Data
@Slf4j
public class WxopenAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;
    private MemberFeignClient memberFeignClient;
    private StringRedisTemplate redisTemplate;
    private HttpApiClientWechat httpApiClientWechat;
    private Environment env;

    /**
     * 微信开放平台认证
     *
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    @SneakyThrows
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        WxopenAuthenticationToken authenticationToken = (WxopenAuthenticationToken) authentication;
        String open_code = (String) authenticationToken.getPrincipal();
        String mobile = authenticationToken.getMobile();
        String verifyCode = authenticationToken.getVerifyCode();
        log.info("wxopen opencode{}:", open_code);
        try {
            //获取OpenID
            String openid = redisTemplate.opsForValue().get(open_code);
            if (openid == null) {
                JSONObject ret = httpApiClientWechat.openLogin(open_code);
                if (ret.get("code").equals(200)) {
                    if (ret.get("errcode") != null) {
                        //微信获取code异常
                        throw new BizException(ResultCode.WXOPEN_AUTH_ERROR);
                    } else {
                        //正常获取OpenId
                        openid = ret.get("openid").toString();
                    }
                } else {
                    //微信获取code异常
                    log.info("wxopen catch error{}", ret.get("code").toString());
                    throw new BizException(ResultCode.WXOPEN_AUTH_ERROR);
                }
            }
            //1.绑定逻辑
            if (mobile != null && verifyCode != null && verifyCode.startsWith("bind:")) {
                String bind_wx_key = "wxbind:" + mobile + ":" + open_code + ":" + verifyCode;
                String bind_v = redisTemplate.opsForValue().get(bind_wx_key);
                if (bind_v != null) {
                    //走绑定过程
                    log.info("wxopen bind logic{} ", bind_wx_key);
                    //更新绑定关系
                    BindWxopenDTO wxopenDTO = new BindWxopenDTO();
                    wxopenDTO.setMobile(mobile);
                    wxopenDTO.setOpenId(openid);
                    Result<Boolean> ret = memberFeignClient.bindOpenId(wxopenDTO);
                    if (Result.isSuccess(ret)) {
                        //删除绑定码和OpenId
                        redisTemplate.delete(bind_wx_key);
                        redisTemplate.delete(open_code);
                        //绑定成功
                        UserDetails userDetails = ((MemberUserDetailsServiceImpl) userDetailsService).loadUserByOpenId(openid);
                        WxopenAuthenticationToken result = new WxopenAuthenticationToken(userDetails, new HashSet<>());
                        result.setDetails(authentication.getDetails());
                        return result;
                    } else {
                        //绑定失败
                        throw new BizException(ResultCode.WXOPEN_AUTH_BIND_FAILUR);
                    }
                } else {
                    //绑定码失效
                    throw new BizException(ResultCode.WXOPEN_AUTH_BIND_FAILUR);
                }
            }
            //2.注册or登录逻辑
            boolean isNew = false;
            Result<MemberAuthDTO> memberAuthResult = memberFeignClient.loadUserByOpenId(openid);
            if (memberAuthResult == null) {
                //微信用户返回null,系统异常
                log.info("wxopen system error");
                throw new BizException(ResultCode.USER_AUTH_ERROR);
            }
            log.info("wxopen openid{} username{} verify{} code{}", openid, mobile, verifyCode, memberAuthResult.getCode());
            if (ResultCode.USER_NOT_EXIST.getCode().equals(memberAuthResult.getCode())) {
                // 1.微信开放用户不存在，目标用户不存在，则注册用户
                // 2.微信开放用户不存在，目标用户存在，则走绑定逻辑
                // 将code和openid 保留在缓存 过期时间300s
                redisTemplate.opsForValue().set(open_code, openid, 300, TimeUnit.SECONDS);
                if (mobile != null && verifyCode != null) {
                    //手机号和确认码非空,短线验证码确认
                    boolean isDev = ArrayUtil.contains(env.getActiveProfiles(), "dev");
                    boolean jumpVerify = false;
                    if (isDev && verifyCode.equals("6666")) {
                        jumpVerify = true;
                    }
                    if (!jumpVerify) {
                        boolean b = VCodeUtils.checkVCode(redisTemplate, VCodeTypeEnum.LOGIN, mobile, verifyCode);
                        if (!b) {
                            throw new BizException(ResultCode.VERIFY_CODE_ERROR);
                        }
                    }
                    //验证通过
                    memberAuthResult = memberFeignClient.loadUserByMobile(mobile);
                    if (memberAuthResult != null && ResultCode.USER_NOT_EXIST.getCode().equals(memberAuthResult.getCode())) {
                        //手机用户不存在，注册用户
                        redisTemplate.delete(open_code);
                        MemberDTO memberDTO = new MemberDTO();
                        memberDTO.setMobile(mobile);
                        memberDTO.setOpenid(openid);
                        memberFeignClient.addMember(memberDTO);
                        isNew = true;
                        MemberUserDetails userDetails = (MemberUserDetails) ((MemberUserDetailsServiceImpl) userDetailsService).loadUserByMobile(mobile);
                        userDetails.setIsNew(isNew);
                        WxopenAuthenticationToken result = new WxopenAuthenticationToken(userDetails, new HashSet<>());
                        result.setDetails(authentication.getDetails());
                        return result;
                    } else {
                        //手机用户存在，查看是否绑定
                        if (memberAuthResult.getData().getOpenId() != null) {
                            //用户已经绑定
                            throw new BizException(ResultCode.WXOPEN_AUTH_BIND_ALREADY);
                        } else {
                            //用户未绑定，生成绑定码，通知前端需要绑定手机号
                            String tmpbindCode = "bind:" + verifyCode;
                            String bind_wx = "wxbind:" + mobile + ":" + open_code + ":" + tmpbindCode;
                            log.info("wxopen bind{} ", bind_wx);
                            redisTemplate.opsForValue().set(bind_wx, open_code, 300, TimeUnit.SECONDS);
                            throw new BizException(ResultCode.WXOPEN_AUTH_BIND);
                        }
                    }
                } else {
                    throw new BizException(ResultCode.USER_NOT_EXIST);
                }
            } else {
                //微信用户存在
                UserDetails userDetails = ((MemberUserDetailsServiceImpl) userDetailsService).loadUserByOpenId(openid);
                WxopenAuthenticationToken result = new WxopenAuthenticationToken(userDetails, new HashSet<>());
                result.setDetails(authentication.getDetails());
                return result;
            }
//            WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(code);
//            log.info("wx:{}", ret.toString());
            //TODO 可以增加自己的逻辑，关联业务相关数据
        } catch (URISyntaxException e) {
            log.info("wxopen catch error");
            throw new BizException(ResultCode.WXOPEN_AUTH_ERROR);
        }
//        //非正常逻辑
//        log.info("wxopen error logic");
//        throw new BizException(ResultCode.USER_AUTH_ERROR);
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return WxopenAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
