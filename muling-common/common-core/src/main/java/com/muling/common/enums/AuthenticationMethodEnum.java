package com.muling.common.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;

/**
 * 认证方式枚举
 *
 * @author <a href="mailto:xianrui0365@163.com">xianrui</a>
 * @date 2021/10/4
 */
public enum AuthenticationMethodEnum implements IBaseEnum<String> {

    USERNAME("username", "用户名"),
    EMAIL("email", "邮箱"),
    MOBILE("mobile", "手机号"),
    OPENID("openId", "微信认证系统唯一身份标识"),
    ALIPAYID("alipayId", "支付宝认证系统唯一身份标识"),
    ;

    @Getter
    private String value;

    @Getter
    private String label;

    AuthenticationMethodEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }
}
