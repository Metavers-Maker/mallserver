package com.muling.mall.bms.pojo.form.admin;

import com.muling.mall.bms.enums.FromTypeEnum;
import com.muling.mall.bms.enums.ViewTypeEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CoinRewardConfigForm {


    private String name;

    /**
     * 获取渠道
     */
    private FromTypeEnum fromType;

    /**
     * 获取积分类型（百分比）
     */
    private Integer coinType;

    /**
     * 获取比率（百分比）
     */
    private BigDecimal coinRate;

    /**
     * 持有比率（百分比）
     */
    private BigDecimal stickRate;

    /**
     * 0 不使用，1使用
     */
    private ViewTypeEnum visible;

}
