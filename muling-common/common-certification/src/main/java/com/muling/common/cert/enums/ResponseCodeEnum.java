package com.muling.common.cert.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum ResponseCodeEnum implements IBaseEnum<Integer> {

    _10000(10000, "请求成功"),
    _10002(10002, "请求报文解析错误"),
    _10003(10003, "请求报文错误"),
    _10004(10004, "请求报文格式有误"),
    _10005(10005, "用户验证失败"),
    _10006(10006, "查询参数错误"),
    _10007(10007, "调用接口失败"),
    _10008(10008, "请求失败"),
    _10009(10009, "请求报文格式错误"),
    _10010(10010, "发送的报文不能为空"),
    _10011(10011, "请求地址错误"),
    _10012(10012, "报文错误"),
    _10013(10013, "数据加密失败"),
    _10014(10014, "该接口已停用"),
    _10015(10015, "该用户已停用"),
    _10016(10016, "接口调用异常"),
    _10017(10017, "查询失败"),
    _10018(10018, "参数错误"),
    _10019(10019, "系统异常");

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
