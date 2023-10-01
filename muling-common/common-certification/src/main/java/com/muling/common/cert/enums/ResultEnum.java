package com.muling.common.cert.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum ResultEnum implements IBaseEnum<Integer> {

    PASS(1, "验证一致"),
    //验证不一致
    FAIL(2, "验证不一致"),
    //异常情况
    ERROR(3, "异常情况");

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    ResultEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
