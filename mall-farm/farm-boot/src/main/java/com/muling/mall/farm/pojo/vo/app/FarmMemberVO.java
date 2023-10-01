package com.muling.mall.farm.pojo.vo.app;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class FarmMemberVO {

    private Long id;

    private Long memberId;

    /**
     * 奖励货币类型
     * */
    private Integer claimCoinType;

    /**
     * 奖励货币数量
     * */
    private BigDecimal claimCoinValue;

    /**
     * 允许领取的时间
     * */
    private Long allowClaimed;

    /**
     * 领取时间
     * */
    private Long claimed;

    /**
     * 农场状态
     * */
    private Integer status;

    /**
     * 烧伤码
     * */
    private Integer burnCode;
}
