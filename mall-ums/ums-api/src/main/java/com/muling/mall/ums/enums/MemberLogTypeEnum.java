package com.muling.mall.ums.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum MemberLogTypeEnum implements IBaseEnum<Integer> {

    LOGIN(0, "登录"),
    REG(1, "注册"),
    ;

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    MemberLogTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
