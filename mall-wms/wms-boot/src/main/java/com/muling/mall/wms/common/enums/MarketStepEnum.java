package com.muling.mall.wms.common.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum MarketStepEnum implements IBaseEnum<Integer> {

    INIT(0, "初始"),
    BUYER_LOCK(1, "对手锁定"),
    BUYER_COMMIT(2, "买币方提交"),
    SELLER_COMMIT_COMPLETE(3, "卖币方确认完成"),
    BUYER_CANCLE(4, "订单取消"),
    FREEZE (5, "订单冻结");

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
