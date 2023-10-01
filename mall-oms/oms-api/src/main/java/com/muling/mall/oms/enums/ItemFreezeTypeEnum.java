package com.muling.mall.oms.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum ItemFreezeTypeEnum implements IBaseEnum<Integer> {

    COMMON(0, "默认"),

    MARKET(1, "市场"),

    FARM(2, "挖矿"),
    ;

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    ItemFreezeTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
