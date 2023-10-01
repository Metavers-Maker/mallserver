package com.muling.mall.farm.pojo.vo.app;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class FarmAdVO {

    private Long id;

    private Long memberId;

    /**
     * 广告类型（Farm前的广告）
     * **/
    private Integer adType;

    /**
     * 订单号
     * **/
    private Long adSn;

    /**
     * 奖励积分类型
     * **/
    private Integer rewardCoinType;

    /**
     * 奖励积分数量
     * **/
    private BigDecimal rewardCoinValue;

    /**
     * 任务阶段
     * */
    private Integer step;

    /**
     * 任务状态
     * */
    private Integer status;

}
