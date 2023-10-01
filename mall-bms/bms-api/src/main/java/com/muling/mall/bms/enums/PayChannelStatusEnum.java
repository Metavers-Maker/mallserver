package com.muling.mall.bms.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum PayChannelStatusEnum implements IBaseEnum<Integer> {

    /**
     * 类型 0-下架 1-上架
     */
    DOWN(0, "下架"),
    UP(1, "上架");

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    PayChannelStatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
