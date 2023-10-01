package com.muling.mall.wms.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MarketConfigVO {

    private Integer coinType;

    private Integer feeType;

    private BigDecimal fee;

    private BigDecimal minFee;

    private BigDecimal minBalance;

    private BigDecimal maxBalance;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    /**
     * 0-买单配置，1-卖单配置
     * */
    private Integer opType;

    /**
     * 0-可用 1-不可用
     */
    private Integer status;

}
