package com.muling.mall.ums.pojo.vo;

import cn.hutool.json.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("会员银行卡对象")
@Data
public class BankVO {

    @ApiModelProperty("会员ID")
    private Long id;

    @ApiModelProperty("银行卡号")
    private String bankCardCode;

    @ApiModelProperty("银行卡")
    private String bankName;

    @ApiModelProperty("是否默认1默认，0非默认")
    private Integer used;

//    @ApiModelProperty("会员ID")
//    private String bankUsername;

}
