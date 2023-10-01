package com.muling.mall.bms.pojo.vo.app;

import lombok.Data;

@Data
public class MarketConfigVO {

    private Long id;

    private String name;

    private Long spuId;

    private Integer coinType;

    private Long fee;

    /**
     * 0-可用 1-不可用
     */
    private Integer status;

}
