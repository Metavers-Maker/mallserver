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
public enum OrderStatusEnum implements IBaseEnum<Integer> {

    PENDING_PAYMENT(101,"待支付"),
    USER_CANCEL(102,"用户取消"),
    AUTO_CANCEL(103,"系统自动取消"),

    PAYED(201,"已支付"),
    APPLY_REFUND(202,"申请退款"),
    REFUNDED(203,"已退款"),

    PENDING_SHIPPED(301,"待发货"),

    DELIVERED(401,"已发货"),

    USER_RECEIVE(501,"用户收货"),
    AUTO_RECEIVE(502,"系统自动收货"),

    FINISHED(901,"已完成")
    ;

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    OrderStatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
