package com.muling.mall.pms.common.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum StatusEnum implements IBaseEnum<Integer> {

    DOWN(0, "下架"),
    UP(1, "上架"),
    START(2, "正常销售"),
    STOP(3, "停止销售"),
    LOCK(4, "发布锁定"),
    ;

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    StatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
