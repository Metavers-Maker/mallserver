package com.muling.mall.farm.pojo.form.admin;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;

@ApiModel("农场包配置表单对象")
@Data
public class FarmBagConfigForm {

    private String name;

    private Long farmId;

    private Long spuId;

    private BigDecimal activateCoinValue;

    private BigDecimal claimCoinValue;

    private BigDecimal claimCoinValueExt;

    private BigDecimal activeValueExt;

    private Integer limitHour;

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



