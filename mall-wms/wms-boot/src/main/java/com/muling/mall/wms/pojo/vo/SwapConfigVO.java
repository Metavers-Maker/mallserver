package com.muling.mall.wms.pojo.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("视图对象")
public class SwapConfigVO {

    private Long id;

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
    private Integer status;

    private String remark;
}
