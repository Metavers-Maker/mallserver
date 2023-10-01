package com.muling.global.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum OssScopeEnum implements IBaseEnum<Integer> {

    PUBLIC(0, "PUBLIC"), //公共读取

    PRIVATE(1, "PRIVATE");  //私有存储

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    OssScopeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
