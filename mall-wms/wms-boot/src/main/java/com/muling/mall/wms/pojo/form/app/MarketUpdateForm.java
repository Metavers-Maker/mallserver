package com.muling.mall.wms.pojo.form.app;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MarketUpdateForm {

    private Integer coinType;

    private BigDecimal balance;

    private BigDecimal singlePrice;
}
