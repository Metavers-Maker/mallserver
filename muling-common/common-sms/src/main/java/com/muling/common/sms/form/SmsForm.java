package com.muling.common.sms.form;

import com.muling.common.util.MobileUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@ApiModel("发送验证码表单对象")
@Data
public class SmsForm {

    @ApiModelProperty("手机")
    @Pattern(regexp = MobileUtils.CHINA_PATTERN, message = "无效的手机号")
    @NotNull(message = "手机号不能为空")

    String phoneNumber;
    @ApiModelProperty("类型(0登录，1注册，2重置登录密码，3重置交易密码，4绑定三方 5寄售验证码)")
    @NotNull(message = "类型不能为空")
    Integer type;

    @ApiModelProperty("行为验证码")
    @NotNull(message = "行为验证不能为空")
    String validate;

}



