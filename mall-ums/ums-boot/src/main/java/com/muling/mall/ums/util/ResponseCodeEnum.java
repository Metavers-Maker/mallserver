package com.muling.mall.ums.util;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum ResponseCodeEnum implements IBaseEnum<Integer> {

    _100(100, "请求成功"),
    _400(400, "APIKey错误"),
    _401(401, "返回类型错误"),
    _402(402, "查询失败"),
    _403(403, "访问次数超限"),
    _404(404, "API参数错误{0}"),

    _405(405, "账户余额不足"),
    _406(406, "请确认您的访问地址是否正确"),
    _408(408, "IP与APIKEY没有绑定"),
    _200(200, "查无记录"),
    _107(107, "同一个样本重复调用次数达到上限");

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    ResponseCodeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
