package com.muling.mall.bms.pojo.form.app;

import lombok.Data;

@Data
public class ExchangeForm {

    private Long exchangeId;

    private Long itemId;

    private Integer itemNum = 1;
}
