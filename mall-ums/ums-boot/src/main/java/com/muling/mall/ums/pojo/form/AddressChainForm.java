package com.muling.mall.ums.pojo.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 三方账户表单对象
 */
@ApiModel("三方账户表单对象")
@Data
public class AddressChainForm {

    @ApiModelProperty("地址")
    private String address;

    @ApiModelProperty("地址类型 1：ETH类型，3：银行卡类型")
    private Integer chainType;

    @ApiModelProperty("银行卡号")
    private String bankCardCode;

    @ApiModelProperty("银行名")
    private String bankName;

    @ApiModelProperty("银行账户名")
    private String bankUsername;

    @ApiModelProperty("手机")
    private String mobile;

    @ApiModelProperty("手机验证码")
    private String code;

}



