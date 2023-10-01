package com.muling.mall.bms.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum StakeItemLogTypeEnum implements IBaseEnum<Integer> {

    STAKE(0, "锁仓"),

    WITHDRAW(1, "提取");

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    StakeItemLogTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
