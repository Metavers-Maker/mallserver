package com.muling.auth.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.muling.common.cert.service.HttpApiClientWechat;
import com.muling.common.constant.GlobalConstants;
import com.muling.common.constant.SecurityConstants;
import com.muling.common.result.Result;
import com.muling.common.web.util.JwtUtils;
import com.muling.common.web.util.RequestUtils;
import com.muling.mall.ums.enums.MemberLogTypeEnum;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.net.URISyntaxException;
import java.security.KeyPair;
import java.security.Principal;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Api(tags = "auth center")
@RestController
@RequestMapping("/oauth")
@AllArgsConstructor
@Slf4j
public class AuthController {

    private TokenEndpoint tokenEndpoint;
    private StringRedisTemplate redisTemplate;
    private RabbitTemplate rabbitTemplate;
    private Environment env;
    private KeyPair keyPair;

    private final HttpApiClientWechat httpApiClientWechat;

    @ApiOperation(value = "OAuth2认证", notes = "登录入口")
    @PostMapping("/token")
    public Object postAccessToken(
            @ApiIgnore Principal principal,
            @ApiParam(value = "授权模式", example = "sms_code/google/username/wechat/wxopen/alipay") @RequestParam String grant_type,
            @ApiParam(value = "google登录token") @RequestParam(required = false) String google_token,
            @ApiParam(value = "google验证code") @RequestParam(required = false) String google_verify,
            @ApiParam(value = "刷新token") @RequestParam(required = false) String refresh_token,
            @ApiParam(value = "登录用户名") @RequestParam(required = false) String username,
            @ApiParam(value = "登录密码") @RequestParam(required = false) String password,
            @ApiParam(value = "邮箱") @RequestParam(required = false) String email,
            @ApiParam(value = "手机号") @RequestParam(required = false) String mobile,
            @ApiParam(value = "手机验证码") @RequestParam(required = false) String verify_code,
            @ApiParam(value = "微信开放平台Code") @RequestParam(required = false) String open_code,
            @ApiParam(value = "邀请码") @RequestParam(required = false) String invite_code
    ) throws HttpRequestMethodNotSupportedException, InvalidTokenException {
        //
        Map<String, String> parameters = new HashMap<>();
        parameters.put("grant_type", grant_type);
        parameters.putIfAbsent("idToken", google_token);
        parameters.putIfAbsent("googleCode", google_verify);
        parameters.putIfAbsent("refresh_token", refresh_token);
        parameters.putIfAbsent("username", username);
        parameters.putIfAbsent("password", password);
        parameters.putIfAbsent("email", email);
        parameters.putIfAbsent("mobile", mobile);
        parameters.putIfAbsent("verify_code", verify_code);
        parameters.putIfAbsent("open_code", open_code);
        parameters.putIfAbsent("invite_code", invite_code);
        /**
         * 获取登录认证的客户端ID
         *
         * 兼容两种方式获取Oauth2客户端信息（client_id、client_secret）
         * 方式一：client_id、client_secret放在请求路径中(注：当前版本已废弃)
         * 方式二：放在请求头（Request Headers）中的Authorization字段，且经过加密，例如 Basic Y2xpZW50OnNlY3JldA== 明文等于 client:secret
         */
        String clientId = RequestUtils.getOAuth2ClientId();
        log.info("OAuth认证授权 客户端ID:{}，请求参数：{}", clientId, JSONUtil.toJsonStr(parameters));

        /**
         * knife4j接口文档测试使用
         *
         * 请求头自动填充，token必须原生返回，不能有任何包装，否则显示 undefined undefined
         * 账号/密码:  client_id/client_secret : client/123456
         */
        boolean isDev = ArrayUtil.contains(env.getActiveProfiles(), "dev");
        if (isDev && SecurityConstants.TEST_CLIENT_ID.equals(clientId)) {
            return tokenEndpoint.postAccessToken(principal, parameters).getBody();
        }
        OAuth2AccessToken accessToken = tokenEndpoint.postAccessToken(principal, parameters).getBody();

        //日志
        Map<String, String> headers = RequestUtils.headers();
        headers.put("client_id", clientId);
        headers.put("type", MemberLogTypeEnum.LOGIN.getValue() + "");
        headers.put("grant_type", RequestUtils.getGrantType());
        headers.put("device_id", RequestUtils.getDeviceId());
        headers.put("ip", RequestUtils.getIp());
        headers.put("member_id", accessToken.getAdditionalInformation().get("memberId") + "");
        String isNew = accessToken.getAdditionalInformation().get("isNew") + "";
        if ("true".equals(isNew)) {
            headers.put("type", MemberLogTypeEnum.REG.getValue() + "");
        } else {
            headers.put("type", MemberLogTypeEnum.LOGIN.getValue() + "");
        }
        rabbitTemplate.convertAndSend(GlobalConstants.MQ_MEMBER_LOG_QUEUE, JSONUtil.toJsonStr(headers));
        return Result.success(accessToken);
    }

    @ApiOperation(value = "logout")
    @DeleteMapping("/logout")
    public Result logout() {
        JSONObject payload = JwtUtils.getJwtPayload();
        String jti = payload.getStr(SecurityConstants.JWT_JTI); // JWT唯一标识
        Long expireTime = payload.getLong(SecurityConstants.JWT_EXP); // JWT过期时间戳(单位：秒)
        if (expireTime != null) {
            long currentTime = System.currentTimeMillis() / 1000;// 当前时间（单位：秒）
            if (expireTime > currentTime) { // token未过期，添加至缓存作为黑名单限制访问，缓存时间为token过期剩余时间
                redisTemplate.opsForValue().set(SecurityConstants.TOKEN_BLACKLIST_PREFIX + jti, "", (expireTime - currentTime), TimeUnit.SECONDS);
            }
        } else { // token 永不过期则永久加入黑名单
            redisTemplate.opsForValue().set(SecurityConstants.TOKEN_BLACKLIST_PREFIX + jti, "");
        }
        return Result.success("注销成功");
    }

    @ApiOperation(value = "获取公钥")
    @GetMapping("/public-key")
    public Map<String, Object> getPublicKey() {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAKey key = new RSAKey.Builder(publicKey).build();
        return new JWKSet(key).toJSONObject();
    }

    @ApiOperation(value = "加密信息")
    @PostMapping("/encrypt")
    public String encrypt(
            @ApiParam(value = "原始文本", example = "123") @RequestParam String data) {
        SymmetricCrypto des = new SymmetricCrypto(SymmetricAlgorithm.DESede, Base64.decode("/WLsEyP7GRYLc56PN/sx5f1i7BMj+xkW"));
        return des.encryptHex(data);
    }

    @ApiOperation(value = "解密信息")
    @PostMapping("/decrypt")
    public String decrypt(
            @ApiParam(value = "加密文本", example = "a41824d63fa03f07") @RequestParam String data) {
        SymmetricCrypto des = new SymmetricCrypto(SymmetricAlgorithm.DESede, Base64.decode("/WLsEyP7GRYLc56PN/sx5f1i7BMj+xkW"));
        return des.decryptStr(data);
    }

}
