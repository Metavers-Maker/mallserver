package com.muling.mall.bms.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import com.muling.mall.bms.enums.ExchangeTypeEnum;
import com.muling.mall.bms.enums.FromTypeEnum;
import com.muling.mall.bms.enums.StatusEnum;
import com.muling.mall.bms.enums.ViewTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 积分奖励配置表
 */
@Data
@Accessors(chain = true)
public class BmsCoinConfig extends BaseEntity {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

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
