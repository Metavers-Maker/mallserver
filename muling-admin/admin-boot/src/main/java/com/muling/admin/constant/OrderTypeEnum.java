package com.muling.admin.constant;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum OrderTypeEnum  implements IBaseEnum<Integer> {

    L1_MARKET(0,"一级市场"),
    L2_MARKET(1,"二级市场订单");

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    OrderTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
