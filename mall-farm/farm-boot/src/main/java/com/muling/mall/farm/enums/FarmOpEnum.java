package com.muling.mall.farm.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum FarmOpEnum implements IBaseEnum<Integer> {

    CREATE(0, "创建工作包"),

    OPEN(1, "开启"),

    CLAIM(2, "领取奖励"),

    ACTIVATE(3, "激活"),

    CLOSE(4, "关闭"),

    RESET(5, "重置"),

    FREEZE(6, "冻结"),
    UNFREEZE(7, "解冻"),
    ;

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    FarmOpEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
