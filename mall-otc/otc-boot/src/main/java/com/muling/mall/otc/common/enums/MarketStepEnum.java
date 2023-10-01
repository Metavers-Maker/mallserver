package com.muling.mall.otc.common.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum MarketStepEnum implements IBaseEnum<Integer> {

    INIT(0, "初始"),
    BUYER_COMMIT(1, "买家确认"),
    SELLER_COMMIT_COMPLETE(2, "卖家确认完成");

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    MarketStepEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
