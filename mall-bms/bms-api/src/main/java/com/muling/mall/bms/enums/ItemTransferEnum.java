package com.muling.mall.bms.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum ItemTransferEnum implements IBaseEnum<Integer> {

    NON_TRANSFER(0, "未转移"),
    TRANSFER(1, "转移中");

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    ItemTransferEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
