package com.muling.mall.bms.enums;

import com.muling.common.base.IBaseEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * @author huawei
 * @desc
 * @email huawei_code@163.com
 * @date 2021/1/16
 */
public enum ItemTypeEnum implements IBaseEnum<Integer> {


    GOODS(0, "商品"),
    BLIND_BOX(1, "盲盒"),
    TRANSFER(2, "转赠卡"),
    GOODS_PARK(3, "合成品"),
    GOODS_RIGHT(4, "权益卡"),
    ;

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    ItemTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
