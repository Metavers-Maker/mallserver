package com.muling.mall.task.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum TaskTypeEnum implements IBaseEnum<Integer> {

    DAY_TYPE(0, "每日任务"),

    ONCE_TYPE(1, "单次任务"),
    ;

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    TaskTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
