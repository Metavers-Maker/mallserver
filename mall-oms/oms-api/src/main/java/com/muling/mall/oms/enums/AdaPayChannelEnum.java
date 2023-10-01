package com.muling.mall.oms.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum AdaPayChannelEnum implements IBaseEnum<Integer> {

    ALIPAY(0, "支付宝 App 支付"),
    ALIPAY_QR(1, "支付宝 App 支付"),










    ;

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    AdaPayChannelEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
