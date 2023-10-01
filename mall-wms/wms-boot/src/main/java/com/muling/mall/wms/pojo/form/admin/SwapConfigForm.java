package com.muling.mall.wms.pojo.form.admin;

import com.muling.common.enums.StatusEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SwapConfigForm {

    private Integer sourceCoinType;

    private BigDecimal sourceMinValue;

    private BigDecimal sourceMaxValue;

    private BigDecimal targetCoinType;

    private BigDecimal ratio;

    private Integer feeType;

    private BigDecimal fee;

    private BigDecimal minFee;

    /**
     * 0-可用 1-不可用
     */
    private StatusEnum status;

    private String remark;
}
