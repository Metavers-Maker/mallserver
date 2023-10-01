package com.muling.mall.bms.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum FromTypeEnum implements IBaseEnum<Integer> {

    FROM_MARKET_1_GET(0, "1级市场获取"),

    FROM_MARKET_2_GET(1, "2级市场获取"),

    FROM_TRANSFORM_GET(2, "转赠获取"),
    FROM_AIRDROP_GET(3, "空投获取"),
    FROM_MARKET_1_BURN(4, "1级市场销毁"),

    FROM_MARKET_2_BURN(5, "2级市场销毁"),

    FROM_TRANSFORM_BURN(6, "转赠销毁"),
    FROM_AIRDROP_BURN(7, "空投销毁"),
    FROM_BOX_GET(8, "盲盒获取"),
    FROM_PUBLISH(100, "首发创建"),
    ;

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    FromTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
