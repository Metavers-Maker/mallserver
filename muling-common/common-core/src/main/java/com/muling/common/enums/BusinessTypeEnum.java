package com.muling.common.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * @author haoxr
 * @date 2021-02-17
 */
public enum BusinessTypeEnum implements IBaseEnum<Integer> {

    USER(100, "user"),
    MEMBER(200, "member"),
    ORDER(300, "order"),
    SUBJECT(400, "subject"),
    BRAND(500, "brand"),
    SPU(600, "spu"),
    SKU(700, "sku"),
    AD(800, "ad"),
    AIRDROP(900, "airdrop"),

    SAND(1000, "sand"),
    ;

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    BusinessTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
