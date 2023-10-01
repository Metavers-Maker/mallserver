package com.muling.mall.ums.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum AuthStatusEnum implements IBaseEnum<Integer> {

    INIT(0, "待审核"),
    PASS(1, "审核通过"),
    FAIL(2, "审核失败");

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    AuthStatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
