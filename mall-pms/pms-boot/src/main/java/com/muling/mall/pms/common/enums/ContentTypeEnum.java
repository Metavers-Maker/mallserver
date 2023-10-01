package com.muling.mall.pms.common.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum ContentTypeEnum implements IBaseEnum<Integer> {

    SUBJECT(0,"主题"),
    PRODUCT(1,"产品"),
    ;

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    ContentTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
