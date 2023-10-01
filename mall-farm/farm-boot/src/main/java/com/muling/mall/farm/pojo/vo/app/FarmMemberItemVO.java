package com.muling.mall.farm.pojo.vo.app;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class FarmMemberItemVO {

    private Long id;

    private Long itemId;

    private Long memberId;

    private String name;

    private Long spuId;

    //奖励积分类型
    private Integer claimCoinType;

    //奖励积分数量
    private BigDecimal claimCoinValue;

    //当前周期
    private Integer currPeriod;

    //工作包状态
    private Integer status;

    private Long closed;
}
