package com.muling.mall.ums.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum MemberAuthStatusEnum implements IBaseEnum<Integer> {

    //未认证、认证中、重新认证、已认证
    UN_AUTH(0, "未认证"),
    AUTHING(1, "认证中"),
    RE_AUTH(2, "重新认证"),
    AUTHED(3, "已认证");

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    MemberAuthStatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public static String getLabel(Integer value) {
        MemberAuthStatusEnum[] statusEnum = values();

        for (MemberAuthStatusEnum item : statusEnum) {
            if (item.getValue().equals(value)) return item.getLabel();
        }

        return null;
    }
}
