package com.muling.mall.wms.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MarketDispatchVO {

    private BigDecimal totalFee;

    private Integer totalNum;

    private Long pageTotal;

}
