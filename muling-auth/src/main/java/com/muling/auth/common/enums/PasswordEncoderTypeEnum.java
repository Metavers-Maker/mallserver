package com.muling.auth.common.enums;

import lombok.Getter;

/**
 * @description 密码编码类型枚举
 */

public enum PasswordEncoderTypeEnum {

    BCRYPT("{bcrypt}", "BCRYPT加密"),
    NOOP("{noop}", "无加密明文");

    @Getter
    private String prefix;

    PasswordEncoderTypeEnum(String prefix, String desc) {
        this.prefix = prefix;
    }

}
