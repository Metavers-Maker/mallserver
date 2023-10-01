package com.muling.common.sms.form;

import com.muling.common.util.MobileUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@ApiModel("发送验证码表单对象")
@Data
public class SmsALiForm {

    @ApiModelProperty("手机")
    @Pattern(regexp = MobileUtils.CHINA_PATTERN, message = "无效的手机号")
    @NotNull(message = "手机号不能为空")
    String phoneNumber;
    @ApiModelProperty("类型")
    @NotNull(message = "类型不能为空")
    Integer type;
    @ApiModelProperty("阿里滑块Token")
    @NotNull(message = "阿里滑块Token不能为空")
    String ncToken;
    @ApiModelProperty("阿里滑块会话")
    @NotNull(message = "阿里滑块会话不能为空")
    String session;
    @ApiModelProperty("阿里滑块签名")
    @NotNull(message = "阿里滑块签名不能为空")
    String sig;
    @ApiModelProperty("阿里滑块AppKey")
    @NotNull(message = "阿里滑块AppKey不能为空")
    String appKey;
    @ApiModelProperty("阿里滑块场景")
    @NotNull(message = "阿里滑块场景不能为空")
    String scene;

}



