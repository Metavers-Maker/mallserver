package com.muling.mall.bms.pojo.form.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TransferConfigForm {

    private Integer type;

    private Long spuId;

    private Long typeValue;

    private String remark;

    @ApiModelProperty("内部转赠冷却-秒")
    private Long icd;

    @ApiModelProperty("外部转赠冷却-秒")
    private Long ocd;

}
