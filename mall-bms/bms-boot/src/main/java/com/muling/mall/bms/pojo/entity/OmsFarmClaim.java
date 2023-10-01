package com.muling.mall.bms.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import com.muling.mall.bms.enums.StatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 锁仓表
 */
@Data
@Accessors(chain = true)
public class OmsFarmClaim extends BaseEntity {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long poolId;

    private Long memberId;

    private Integer currentDays;

    private Integer coinType;

    private BigDecimal rewardAmount;

    private StatusEnum status;

}
