package com.muling.mall.ums.pojo.form;

import com.muling.common.util.MobileUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;

@ApiModel("重置交易密码表单对象")
@Data
public class ResetTradePasswordForm {

    @ApiModelProperty("手机")
    @Pattern(regexp = MobileUtils.CHINA_PATTERN, message = "无效的手机号")
    String mobile;

    @ApiModelProperty("交易密码")
    String tradePassword;

    @ApiModelProperty("验证码")
    String code;

}



