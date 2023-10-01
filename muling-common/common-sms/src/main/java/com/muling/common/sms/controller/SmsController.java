package com.muling.common.sms.controller;

import cn.hutool.json.JSONUtil;
import com.muling.common.exception.BizException;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.common.sms.form.SmsALiForm;
import com.muling.common.sms.form.SmsForm;
import com.muling.common.sms.service.SmsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Api(tags = "sms code")
@RestController
@RequestMapping("/sms")
@RequiredArgsConstructor
@Slf4j
public class SmsController {

    private final SmsService smsService;

    @ApiOperation(value = "发送短信验证码")
    @PostMapping("/code")
    public Result sendSmsCode(@Validated @RequestBody SmsForm smsForm) {

        String[] prefix = {"162", "165", "166", "167", "170", "171"};

        String phoneNumber = smsForm.getPhoneNumber();
        String validate = smsForm.getValidate();
        Integer type = smsForm.getType();

        Arrays.stream(prefix).forEach(s -> {
            if (phoneNumber.startsWith(s)) {
                throw new BizException(ResultCode.PARAM_ERROR, "虚拟号不允许使用");
            }
        });
        boolean result = smsService.sendSmsCode(type, phoneNumber);
        if (result) {
            return Result.success();
        } else {
            return Result.failed(ResultCode.SMS_CODE_ERROR);
        }
    }

    @ApiOperation(value = "短信回调")
    @PostMapping("/notify")
    public Result smsNotify(@RequestBody String content,
                            @RequestHeader HttpHeaders headers) {
        log.info("sms notify content:{} headers:{}", content, JSONUtil.toJsonStr(headers));
        return Result.success();
    }
}
