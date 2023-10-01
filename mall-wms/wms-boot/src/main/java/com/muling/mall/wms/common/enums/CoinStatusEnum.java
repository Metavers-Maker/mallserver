package com.muling.mall.wms.common.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 商品属性类型枚举
 *
 * @author <a href="mailto:xianrui0365@163.com">haoxr</a>
 */
public enum CoinStatusEnum implements IBaseEnum<Integer> {

    ENABLED(0, "可用"),
    DISABLED(1, "不可用"),
    ;

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    CoinStatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
