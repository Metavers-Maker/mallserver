package com.muling.mall.oms.enums;


import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * @author huawei
 * @desc 订单来源类型枚举
 * @email huawei_code@163.com
 * @date 2021/1/16
 */
public enum PayTypeEnum implements IBaseEnum<Integer> {

    WEIXIN_JSAPI(1, "微信JSAPI支付"),
    ALIPAY(2, "支付宝支付"),
    APPLEPAY(3, "苹果支付"),
    WEIXIN_APP(4, "微信APP支付"),
    ADA_PAY(5, "ADA支付"),
    ADA_PAY_WEI_XIN(6, "ADA微信预支付"),
    SAND_PAY(7, "杉德支付"),
    AIRDROP_PAY(8, "空投"),
    FREE_PAY(9,"免费支付"),
    ;

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    PayTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

}
