package com.muling.mall.ums.pojo.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 解绑三方账户表单对象
 */
@ApiModel("解绑三方账户表单对象")
@Data
public class AddressChainUnbindForm {

    @ApiModelProperty("account id")
    private Long id;

    @ApiModelProperty("交易密码")
    private String tradePassword;

}



