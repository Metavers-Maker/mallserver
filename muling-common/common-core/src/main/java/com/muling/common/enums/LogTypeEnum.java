package com.muling.common.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum LogTypeEnum implements IBaseEnum<Integer> {

    REGIST(1, "regist"),
    LOGIN(2, "Login"),
    OPERATE(3, "Operate");

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    LogTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
