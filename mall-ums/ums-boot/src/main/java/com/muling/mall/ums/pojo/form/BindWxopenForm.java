package com.muling.mall.ums.pojo.form;

import com.muling.common.util.MobileUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;

@ApiModel("重置密码表单对象")
@Data
public class BindWxopenForm {

    @ApiModelProperty("短线验证码")
    String verifyCode;

    @ApiModelProperty("openCode")
    String wxopenCode;

}



