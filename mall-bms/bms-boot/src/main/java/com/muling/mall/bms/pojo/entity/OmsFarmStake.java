package com.muling.mall.bms.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 锁仓表
 */
@Data
@Accessors(chain = true)
public class OmsFarmStake extends BaseEntity {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long poolId;

    private Long memberId;

    private Long spuId;

    private Integer total;

    private Integer currentDays;

    private Double allocPoint;

}
