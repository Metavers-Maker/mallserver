package com.muling.mall.oms.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum ItemFreezeStatusEnum implements IBaseEnum<Integer> {

    UN_FREEZE(0, "未冻结"),

    FREEZE(1, "已冻结"),
    ;

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    ItemFreezeStatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
