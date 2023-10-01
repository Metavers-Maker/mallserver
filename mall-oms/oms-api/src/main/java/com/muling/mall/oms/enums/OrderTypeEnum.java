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

public enum OrderTypeEnum  implements IBaseEnum<Integer> {

    WEB(0,"PC订单"), // PC订单
    APP(1,"APP订单"), // APP订单
    ;

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    OrderTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
