package com.muling.mall.oms.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum MarketStatusEnum implements IBaseEnum<Integer> {

    DOWN(0, "下架"),
    UP(1, "上架"),
    CLOSE(2, "关闭"),
    COMPLETE(3, "完成");

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    MarketStatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
