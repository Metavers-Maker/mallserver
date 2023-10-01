package com.muling.mall.wms.pojo.form.admin;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MarketConfigForm {

    private Integer coinType;

    private Integer feeType;

    private BigDecimal fee;

    private BigDecimal minFee;

    private BigDecimal minBalance;

    private BigDecimal maxBalance;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private Integer opType;

}
