package com.muling.mall.oms.constant;

import java.util.ArrayList;
import java.util.List;

public enum OrderHeader {
    /**
     * 订单类型 0 1级订单，1 2级订单
     */
    SPU_NAME("商品名称"),

    SPU_ID("商品ID"),
    /**
     * 订单号
     */
    ORDER_SN("订单号"),

    /**
     * 会员id
     */
    MEMBER_ID("用户ID"),

    MEMBER_MOBILE("用户手机号");

    private String value;

    private static final List<String> HEADERS = new ArrayList<>();

    OrderHeader(String value) {
        this.value = value;
    }

    static {
        for (OrderHeader header : OrderHeader.values()) {
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
