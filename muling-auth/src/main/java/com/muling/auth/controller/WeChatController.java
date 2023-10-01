package com.muling.auth.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.muling.common.cert.service.HttpApiClientWechat;
import com.muling.common.result.Result;
import com.muling.common.web.util.MemberUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.net.URISyntaxException;

//@Api(tags = "auth wechat")
@RestController
@RequestMapping("/wechat")
@AllArgsConstructor
@Slf4j
public class WeChatController {

    @Resource
    private WxMaService wxMaService;

    private final HttpApiClientWechat httpApiClientWechat;

    /**
     * 登陆接口
     */
    @GetMapping("/login")
    public Result login(String code) {
        if (StringUtils.isBlank(code)) {
            return Result.failed();
        }
        Long memberId = MemberUtils.getMemberId();
        try {
            WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(code);
            log.info("memberId:{} .wx:{}", memberId, JSONUtil.toJsonStr(session));
            //TODO 可以增加自己的逻辑，关联业务相关数据
            return Result.success(session);
        } catch (WxErrorException e) {
            log.error(e.getMessage(), e);
            return Result.failed();
        }
    }

    @GetMapping("/info")
    public Result info(String sessionKey,
                       String signature, String rawData, String encryptedData, String iv) {

        // 用户信息校验
        if (!wxMaService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
            return Result.failed();
        }
        // 解密用户信息
        WxMaUserInfo userInfo = wxMaService.getUserService().getUserInfo(sessionKey, encryptedData, iv);

        return Result.success(userInfo);
    }

    /**
     * 微信开放平台登陆接口
     */
    @ApiOperation(value = "OAuth2微信Open", notes = "微信开放平台")
    @GetMapping("/open/login")
    public Result openLogin(@ApiParam(value = "微信登录code") @RequestParam(required = true) String code) {
        log.info("openLogin entry:{}", code);
        if (StringUtils.isBlank(code)) {
            return Result.failed();
        }
        try {
            JSONObject ret = httpApiClientWechat.openLogin(code);
            if (ret.get("code").equals(200)) {
                if (ret.get("errcode") != null) {
                    //error
//                    account = data.get("account").toString();
//                    //插入数据
//                    UmsAccountChain accountChain = new UmsAccountChain();
//                    accountChain.setAddress(account);
//                    accountChain.setChainType(AccountChainEnum.ACCOUNT_BSN.getValue());
//                    accountChain.setMemberId(memberId);
//                    accountChain.setStatus(0);
//                    saveFlag = this.save(accountChain);
//                    "access_token": "ACCESS_TOKEN",
//                            "expires_in": 7200,
//                            "refresh_token": "REFRESH_TOKEN",
//                            "openid": "OPENID",
//                            "scope": "snsapi_userinfo",
//                            "unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL"
                } else {
                    //
                }
            } else {
                //
            }
//            WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(code);
            log.info("wx:{}", ret.toString());
            //TODO 可以增加自己的逻辑，关联业务相关数据
            return Result.success(ret);
        } catch (URISyntaxException e) {
            return Result.failed();
        }
    }
}
