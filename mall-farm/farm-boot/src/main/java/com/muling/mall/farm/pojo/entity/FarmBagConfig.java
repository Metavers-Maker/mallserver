package com.muling.mall.farm.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import com.muling.common.enums.VisibleEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FarmBagConfig extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long farmId;

    private String name;

    private Long spuId;

    private BigDecimal activateCoinValue;

    private BigDecimal claimCoinValue;

    private BigDecimal claimCoinValueExt;

    //最小天数
    private Integer minDays;

    //最大天数
    private Integer maxDays;

    //步长(激活一次增加天数)
    private Integer step;

    //活跃度
    private BigDecimal activeValue;

    //N天的活跃度
    private BigDecimal activeValueExt;

    //返佣币种数量
    private BigDecimal rakeBackCoinValue;

    //返佣活跃度
    private BigDecimal rakeBackActiveValue;

    //激活周期
    private Integer period;

    private VisibleEnum visible;
}
