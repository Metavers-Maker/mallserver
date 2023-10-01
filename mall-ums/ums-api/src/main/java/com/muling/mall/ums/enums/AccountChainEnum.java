package com.muling.mall.ums.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum AccountChainEnum implements IBaseEnum<Integer> {

    ACCOUNT_BSN(0, "BSN地址"),
    ACCOUNT_ETH(1, "ETH地址"),
    ACCOUNT_SAND(2, "衫德地址"),
    ACCOUNT_BANK(3, "银行卡地址"),
   ;

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    AccountChainEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
