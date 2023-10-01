package com.muling.mall.bms.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum ChainSwapTypeEnum implements IBaseEnum<Integer> {

    /**
     * 上链交换类型
     */
    PUBLISH_BUY(0, "首发"),
    MARKET(1, "市场"),
    BBOX(2, "盲盒"),
    COMBINE(3, "合成"),
    TRANSFER(4, "转赠"),
    AIRDROP(5, "空投"),
    ;

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    ChainSwapTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
