package com.muling.mall.farm.pojo.vo.app;


import lombok.Data;
import software.amazon.ion.Decimal;

@Data
public class FarmConfigVO {

    private Long id;

    private String name;

    private Integer activateCoinType;

    private Integer claimCoinType;

    //返佣币种
    private Integer rakeBackCoinType;

    private Integer limitHour;

    private Integer maxNum;

//    private Decimal adReward;
//
//    private Decimal adUpReward;
//
//    private Decimal adL1Reward;
//
//    private Decimal adL2Reward;
//
//    private Decimal adL3Reward;
}
