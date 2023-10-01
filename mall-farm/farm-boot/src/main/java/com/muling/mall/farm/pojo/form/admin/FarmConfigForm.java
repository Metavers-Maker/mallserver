package com.muling.mall.farm.pojo.form.admin;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;

@ApiModel("农场配置表单对象")
@Data
public class FarmConfigForm {

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

}



