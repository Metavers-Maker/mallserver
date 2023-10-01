package com.muling.mall.wms.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import com.muling.common.enums.StatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class WmsMarketConfig extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer coinType;

    private Integer feeType;

    private BigDecimal fee;

    private BigDecimal minFee;

    private BigDecimal minBalance;

    private BigDecimal maxBalance;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private Integer opType;

    private StatusEnum status;
}
