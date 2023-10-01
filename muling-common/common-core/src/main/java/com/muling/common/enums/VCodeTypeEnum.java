package com.muling.common.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum VCodeTypeEnum implements IBaseEnum<Integer> {

    REGISTER(0, "注册"),
    LOGIN(1, "登录"),
    RESET_PASSWORD(2, "重置登录密码"),
    RESET_TRADE_PASSWORD(3, "重置交易密码"),
    BIND_THIRD_PLATFORM(4, "绑定三方平台"),

    MARKET_SELL(5, "寄售验证码"),

    UN_REGISTER(6, "注销账号"),

    ;

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    VCodeTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
