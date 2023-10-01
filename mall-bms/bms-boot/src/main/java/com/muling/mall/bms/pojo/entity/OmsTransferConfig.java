package com.muling.mall.bms.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import com.muling.mall.bms.enums.StatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 用户商品表
 */
@Data
@Accessors(chain = true)
public class OmsTransferConfig extends BaseEntity {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 消耗虚拟币种 0-积分
     */
    private Integer type;

    private Long spuId;

    private BigDecimal typeValue;

    private Long icd;

    private Long ocd;

    /**
     * 0-可用 1-不可用
     */
    private StatusEnum status;

    private String remark;

}
