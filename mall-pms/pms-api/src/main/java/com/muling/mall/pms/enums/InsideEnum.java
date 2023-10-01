package com.muling.mall.pms.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum InsideEnum implements IBaseEnum<Integer> {

    /**
     * 类型 0-未绑定 1-绑定
     */
    INSIDE(0, "内部"),
    OUTSIDE(1, "外部");

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    InsideEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
