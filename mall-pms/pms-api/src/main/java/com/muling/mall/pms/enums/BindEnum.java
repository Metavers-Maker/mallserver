package com.muling.mall.pms.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum BindEnum implements IBaseEnum<Integer> {

    /**
     * 类型 0-未绑定 1-绑定
     */
    UN_BIND(0, "未绑定"),
    BIND(1, "绑定");

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    BindEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
