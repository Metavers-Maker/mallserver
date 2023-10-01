package com.muling.mall.farm.pojo.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FarmRakeDTO {

    private Long memberId;

    /**
     * 目标Id
     * */
    private Long targetId;

    /**
     * 类型
     * */
    private Integer coinType;

    /**
     * 数量
     * */
    private BigDecimal coinValue;

}
