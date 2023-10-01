package com.muling.mall.farm.pojo.vo.app;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class FarmBagConfigVO {

    private Long id;

    private String name;

    private Long spuId;

    private BigDecimal activateCoinValue;

    private BigDecimal claimCoinValue;

    //最小天数
    private Integer minDays;

    //最大天数
    private Integer maxDays;

    //步长(激活一次增加天数)
    private Integer step;

    private BigDecimal activeValue;

    //返佣币种数量
    private BigDecimal rakeBackCoinValue;

    //返佣活跃度
    private BigDecimal rakeBackActiveValue;

    //激活周期
    private Integer period;

}
