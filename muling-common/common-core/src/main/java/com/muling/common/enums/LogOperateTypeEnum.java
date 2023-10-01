package com.muling.common.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum LogOperateTypeEnum implements IBaseEnum<Integer> {

    LOGIN_GOOGLE(1, "GOOGLE"),
    LOGIN_FACEBOOK(2, "FACEBOOK"),
    LOGIN_USERNAME(3, "USERNAME"),
    LOGIN_MAIL_CODE(4, "MAIL_CODE"),
    ACTIVATE(5, "ACTIVATE"),

    UNKNOWN(10, "UNKNOWN"),
    LIST(11, "LIST"),
    ADD(12, "ADD"),
    EDIT(13, "EDIT"),
    DELETE(14, "DELETE"),
    MINT(15, "MINT");

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    LogOperateTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
