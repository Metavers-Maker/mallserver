package com.muling.mall.ums.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum FollowStatusEnum implements IBaseEnum<Integer> {

    FOLLOW(0, "关注"),
    BOTH(1, "相互关注"),
    CANCEL(2, "取消关注"),

    ;

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    FollowStatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
