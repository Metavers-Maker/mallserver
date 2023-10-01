package com.muling.mall.oms.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * @author huawei
 * @desc
 * @email huawei_code@163.com
 * @date 2021/1/16
 */
public enum ApplePayStatusEnum implements IBaseEnum<Integer> {

    _0(0, "正常"),
    _21000(21000, "App Store不能读取你提供的JSON对象"),
    _21002(21002, "receipt-data域的数据有问题"),
    _21003(21003, "receipt无法通过验证"),

    _21004(21004, "提供的shared secret不匹配你账号中的shared secret"),
    _21005(21005, "receipt服务器当前不可用"),
    _21006(21006, "receipt合法，但是订阅已过期。服务器接收到这个状态码时，receipt数据仍然会解码并一起发送"),
    _21007(21007, "receipt是Sandbox receipt，但却发送至生产系统的验证服务"),
    _21008(21008, "receipt是生产receipt，但却发送至Sandbox环境的验证服务")


    ;

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    ApplePayStatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
