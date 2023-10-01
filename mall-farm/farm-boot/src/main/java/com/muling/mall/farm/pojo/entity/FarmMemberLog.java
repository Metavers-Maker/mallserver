package com.muling.mall.farm.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FarmMemberLog extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long farmId;

    private Long itemId;

    private Long memberId;

    private String name;

    private Long spuId;

    private Integer type;

    private Integer claimCoinType;
    private BigDecimal claimCoinValue;

}
