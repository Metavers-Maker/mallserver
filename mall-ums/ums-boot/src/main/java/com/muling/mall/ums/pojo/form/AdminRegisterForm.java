package com.muling.mall.ums.pojo.form;

import com.muling.common.util.MobileUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;

@ApiModel("后台注册账号表单对象")
@Data
public class AdminRegisterForm {

    @ApiModelProperty("手机")
    @Pattern(regexp = MobileUtils.CHINA_PATTERN, message = "无效的手机号")
    String mobile;
    @ApiModelProperty("邀请码")
    String inviteCode;

}



