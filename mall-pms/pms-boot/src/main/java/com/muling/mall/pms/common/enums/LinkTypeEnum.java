package com.muling.mall.pms.common.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum LinkTypeEnum implements IBaseEnum<Integer> {

    OUTSIDE_LINK(0, "外链"),
    INSIDE_LINK(1, "内链"),
    VIEW_LINK(2, "展示链接"),
    FANS_LINK(3, "粉丝链接"),
    CUSTOMER_LINK(4, "客服链接"),
    OH_ANNOUNCEMENT_LINK(5, "OH公告链接");

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    LinkTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
