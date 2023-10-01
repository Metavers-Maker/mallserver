package com.muling.mall.ums.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum MailVerifyTypeEnum implements IBaseEnum<Integer> {

    UN_BING_GOOGLE(0, "unbind google auth"),
    BING_CHAIN_ADDRESS(1, "bind chain address"),
    ;

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    MailVerifyTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
