package com.muling.mall.ums.pojo.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 银行卡绑定表单
 */
@ApiModel("银行卡绑定表单")
@Data
public class BankBindForm {

    @ApiModelProperty("平台类型：0 sand")
    private Integer platType;

    @ApiModelProperty("银行卡号")
    private String cardNo;

    @ApiModelProperty("银行卡类型：0 借记 1贷记")
    private String cardType;

    @ApiModelProperty("贷记cvs")
    private String cvs;

    @ApiModelProperty("贷记日期")
    private String cardExpire;

    @ApiModelProperty("银行名")
    private String bankName;

    @ApiModelProperty("银行账户名")
    private String bankUsername;

    @ApiModelProperty("手机")
    private String mobile;

    @ApiModelProperty("手机验证码")
    private String code;

}



