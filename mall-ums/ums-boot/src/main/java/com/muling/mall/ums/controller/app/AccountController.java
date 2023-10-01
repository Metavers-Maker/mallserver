package com.muling.mall.ums.controller.app;

import com.muling.common.result.Result;
import com.muling.common.web.annotation.RequestLimit;
import com.muling.mall.ums.pojo.form.*;
import com.muling.mall.ums.service.IUmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "app-账户")
@RestController
@RequestMapping("/app-api/v1/account")
@Slf4j
@RequiredArgsConstructor
public class AccountController {

    private final IUmsMemberService umsMemberService;

    @ApiOperation(value = "注册账户")
    @PostMapping("/register")
    @RequestLimit(count = 5, waits = 1, limitFiledType = RequestLimit.LimitFiledType.IP)
    public Result register(@Validated @RequestBody RegisterForm registerForm) {
        log.info("注册：{}.{}.{}", registerForm.getMobile(), registerForm.getInviteCode(), registerForm.getCode());
        boolean register = umsMemberService.register(registerForm);
        if (register) {
//            redisTemplate.opsForValue().set(SecurityConstants.DEVICE_ID_PREFIX + deviceId, deviceId);
        }
        return Result.judge(register);
    }

    @ApiOperation(value = "注销账户")
    @PostMapping("/unregister")
    @RequestLimit(count = 5, waits = 1, limitFiledType = RequestLimit.LimitFiledType.IP)
    public Result unregister(@Validated @RequestBody UnRegisterForm form) {
        log.info("注销：{}.{}", form.getMobile(), form.getCode());
        boolean register = umsMemberService.unregister(form);
        return Result.judge(register);
    }

    @ApiOperation(value = "重置密码")
    @PostMapping("/reset-password")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.CUSTOM_VALUE, field = "mobile")
    public Result resetPassword(@Validated @RequestBody ResetPasswordForm resetPasswordForm) {
        boolean result = umsMemberService.resetPassword(resetPasswordForm);
        return Result.judge(result);
    }

    @ApiOperation(value = "重置交易密码")
    @PostMapping("/reset-trade-password")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.CUSTOM_VALUE, field = "mobile")
    public Result resetTradPassword(@Validated @RequestBody ResetTradePasswordForm tradePasswordForm) {
        boolean result = umsMemberService.resetTradePassword(tradePasswordForm);
        return Result.judge(result);
    }

    @ApiOperation(value = "验证交易密码")
    @PostMapping("/check/trade-password/{password}")
//    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.CUSTOM_VALUE, field = "mobile")
    public Result<Boolean> checkTradPassword(@ApiParam("交易密码") @PathVariable String password) {
        boolean result = umsMemberService.checkTradePassword(password);
        return Result.judge(result);
    }

    @ApiOperation(value = "绑定微信Open")
    @PostMapping("/bind/wxopen")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.CUSTOM_VALUE, field = "mobile")
    public Result bindOpen(@Validated @RequestBody BindWxopenForm wxopenForm) {
        boolean result = umsMemberService.bindWxopenDirect(wxopenForm);
        return Result.judge(result);
    }

    @ApiOperation(value = "解绑微信Open")
    @PostMapping("/unbind/wxopen")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.CUSTOM_VALUE, field = "mobile")
    public Result unbindOpen(@Validated @RequestBody BindWxopenForm wxopenForm) {
        boolean result = umsMemberService.unbindWxopenDirect(wxopenForm);
        return Result.judge(result);
    }

}
