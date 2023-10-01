package com.muling.mall.pms.pojo.dto;

import lombok.Data;

@Data
public class RndDTO {
    private Long id;

    private Long target;

    private Long spuId;

    private Integer spuCount;

    private String name;

    /**
     * 奖励藏品最大数量
     * */
    private Integer maxCount;

    /**
     * 奖励藏品产出数量
     * */
    private Integer aliveCount;

    private Long skuId;

    private Integer coinType;

    private Integer coinCount;

    private Integer prod;
}
