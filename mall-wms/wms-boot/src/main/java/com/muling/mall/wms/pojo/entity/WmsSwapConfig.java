package com.muling.mall.wms.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import com.muling.common.enums.StatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 用户商品表
 */
@Data
@Accessors(chain = true)
public class WmsSwapConfig extends BaseEntity {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer sourceCoinType;

    private BigDecimal sourceMinValue;

    private BigDecimal sourceMaxValue;

    private BigDecimal targetCoinType;

    private BigDecimal ratio;

    private Integer feeType;

    private BigDecimal fee;

    private BigDecimal minFee;

    /**
     * 0-可用 1-不可用
     */
    private StatusEnum status;

    private String remark;

}
