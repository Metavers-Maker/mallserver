package com.muling.mall.bms.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum ViewTypeEnum implements IBaseEnum<Integer> {

    /**
     * 可见类型 0-可见 1-隐藏
     */
    INVISIBLE(0, "隐藏"),
    VISIBLE(1, "可见");

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    ViewTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
