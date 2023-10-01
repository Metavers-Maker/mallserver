package com.muling.mall.farm.pojo.vo.app;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class FarmAdItemVO {

    private Long id;

    private Long memberId;

    private String adSn;

    private Integer rewardCoinType;

    private BigDecimal rewardCoinValue;

    private Integer status;
}
