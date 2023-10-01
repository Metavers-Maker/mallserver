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
public enum ItemStatusEnum implements IBaseEnum<Integer> {

    /**
     * 未铸造 铸造中 已铸造
     */
    UN_MINT(0, "未铸造"),
    /**
     * 铸造
     */
    MINTING(1, "铸造中"),
    /**
     * 铸造
     */
    MINTED(2, "已铸造");

    @Getter
    @Setter
    private Integer value;

    @Getter
    private String label;

    ItemStatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
