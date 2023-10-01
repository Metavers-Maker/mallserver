package com.muling.mall.bms.pojo.form.app;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MarketCreateForm {

    private Long itemId;

    private Long price;

    private String code;
}
