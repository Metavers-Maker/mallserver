package com.muling.mall.ums.enums;

import com.muling.common.base.IBaseEnum;
import com.muling.common.enums.StatusEnum;
import lombok.Getter;
import lombok.Setter;

public enum MemberStatusEnum implements IBaseEnum<Integer> {

    FORBIDDEN(0, "禁止"),
    COMMON(1, "正常"),
    UNACTIVATED(2, "待激活"),
    DESTROY(3, "销毁");

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    MemberStatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }


    public String getLabel(Integer value) {
        MemberStatusEnum[] statusEnum = values();

        for (MemberStatusEnum item : statusEnum) {
            if (item.getValue().equals(value)) return item.getLabel();
        }

        return null;
    }
}
