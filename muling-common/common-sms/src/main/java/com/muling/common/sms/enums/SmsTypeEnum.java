package com.muling.common.sms.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * @author huawei
 * @desc
 * @email huawei_code@163.com
 * @date 2021/1/16
 */
public enum SmsTypeEnum implements IBaseEnum<String> {


    ALI_YUN("ali-yun", "阿里云"),

    ALL_NET("all-net", "全网通"),

    YUN_XIN("yun-xin", "网易云信");

    @Getter
    @Setter
    private String value;

    @Getter
    private String label;

    SmsTypeEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }
}
