package com.muling.common.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum VisibleEnum implements IBaseEnum<Integer> {

    /**
     * 可见类型 0-可见 1-隐藏
     */
    HIDE(0, "隐藏"),
    DISPLAY(1, "可见");

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    VisibleEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
