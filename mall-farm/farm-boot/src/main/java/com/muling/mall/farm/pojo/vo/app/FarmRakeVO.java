package com.muling.mall.farm.pojo.vo.app;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class FarmRakeVO {

    private Long id;

    private Long memberId;

    /**
     * 奖励积分类型
     * **/
    private Integer rewardCoinType;

    /**
     * 奖励积分数量
     * **/
    private BigDecimal rewardCoinValue;

    /**
     * 任务状态
     * */
    private Integer status;

}
