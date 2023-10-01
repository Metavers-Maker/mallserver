package com.muling.mall.ums.constant;

import java.util.ArrayList;
import java.util.List;

public enum MemberHeader {
    ID ("会员ID"),

    NICK_NAME ("会员昵称"),

    EMAIL ("会员邮箱"),


    UID ("会员展示ID"),


    MOBILE ("会员手机号"),

    /**
     * 状态(1:正常；0：禁用)
     */
    STATUS ("用户状态"),


    AUTH_STATUS ("实名状态"),


    CREATED ("创建时间");

   private String value;

    private static final List<String> HEADERS = new ArrayList<>();

    MemberHeader(String value) {
        this.value = value;
    }

    static {
        for (MemberHeader header : MemberHeader.values()) {
            HEADERS.add(header.getValue());
        }
    }

    public String getValue() {
        return this.value;
    }

    public static List<String> headers() {
        return HEADERS;
    }

}
