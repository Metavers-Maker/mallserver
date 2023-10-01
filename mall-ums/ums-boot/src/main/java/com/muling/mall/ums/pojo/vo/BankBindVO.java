package com.muling.mall.ums.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("申请绑银行卡")
@Data
public class BankBindVO {

    @ApiModelProperty("申请绑定流水号")
    private String bindSn;

}
