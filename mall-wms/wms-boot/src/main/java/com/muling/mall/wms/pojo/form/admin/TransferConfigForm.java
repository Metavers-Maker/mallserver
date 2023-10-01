package com.muling.mall.wms.pojo.form.admin;

import com.muling.common.enums.StatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TransferConfigForm {

    private Integer type;

    private Integer minValue;

    private Integer maxValue;

    private Integer feeType;

    private Double fee;

    private Long minFee;

    /**
     * 0-可用 1-不可用
     */
    private StatusEnum status;

    private String remark;
}
