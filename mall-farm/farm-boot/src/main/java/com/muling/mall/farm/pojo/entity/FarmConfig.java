package com.muling.mall.farm.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import com.muling.common.enums.VisibleEnum;
import lombok.Data;
import software.amazon.ion.Decimal;

import java.math.BigDecimal;

@Data
public class FarmConfig extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Integer activateCoinType;

    private Integer claimCoinType;

    private Integer claimCoinTypeExt;

    //返佣币种
    private Integer rakeBackCoinType;

    private Integer limitHour;

    private Integer maxNum;

    private BigDecimal adReward;

    private BigDecimal adUpReward;

    private BigDecimal adL1Reward;

    private BigDecimal adL2Reward;

    private BigDecimal adL3Reward;

    private BigDecimal adL4Reward;

    private VisibleEnum visible;
}
