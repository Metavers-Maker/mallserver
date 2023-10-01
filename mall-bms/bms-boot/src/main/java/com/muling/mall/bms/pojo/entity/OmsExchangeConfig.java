package com.muling.mall.bms.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import com.muling.mall.bms.enums.ExchangeTypeEnum;
import com.muling.mall.bms.enums.StatusEnum;
import com.muling.mall.bms.enums.ViewTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 兑换表
 */
@Data
@Accessors(chain = true)
public class OmsExchangeConfig extends BaseEntity {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    private ExchangeTypeEnum exchangeType;

    private Integer coinType;

    private BigDecimal coinValue;

    private Integer periodType;

    private Integer periodValue;

    private Integer maxLimit;

    private Long spuId;

    private String remark;
    /**
     * 0-可用 1-不可用
     */
    private StatusEnum status;

    private ViewTypeEnum visible;


}
