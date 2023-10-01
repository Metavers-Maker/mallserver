package com.muling.mall.ums.pojo.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 确认银行卡绑定表单
 */
@ApiModel("确认银行卡绑定表单")
@Data
public class BankBindEnsureForm {

    @ApiModelProperty("返回流水号")
    private String sdMsgNo;

    @ApiModelProperty("手机")
    private String mobile;

    @ApiModelProperty("手机验证码")
    private String code;

}



