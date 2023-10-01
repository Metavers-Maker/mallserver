package com.muling.mall.bms.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum ExchangeTypeEnum implements IBaseEnum<Integer> {

    COIN_TO_ITEM(0, "币物兑换"),

    ITEM_TO_COIN(1, "物币兑换");

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    ExchangeTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
