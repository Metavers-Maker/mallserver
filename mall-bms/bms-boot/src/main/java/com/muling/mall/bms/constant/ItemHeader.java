package com.muling.mall.bms.constant;

import java.util.ArrayList;
import java.util.List;

public enum ItemHeader {
    //用户昵称/ID/手机号/藏品名称/藏品ID/藏品spu名称/藏品编号/转入时间/转入价格/首发价格/藏品来源
    MEMBER_ID("用户ID"),
    MEMBER_NAME("用户昵称"),
    MEMBER_MOBILE("用户手机号"),
    SPU_ID("藏品ID"),
    SPU_NAME("藏品名称"),
    SKU_NAME("藏品sku名称"),
    ITEM_NO("藏品编号"),
    TRANSFER_TIME("转入时间"),
    TRANSFER_PRICE("转入价格"),
    FIRST_PRICE("首发价格"),
    FROM_TYPE("藏品来源");

    private String value;

    private static final List<String> HEADERS = new ArrayList<>();

    ItemHeader(String value) {
        this.value = value;
    }

    static {
        for (ItemHeader header : ItemHeader.values()) {
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
