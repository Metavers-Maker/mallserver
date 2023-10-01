package com.muling.mall.bms.pojo.vo.app;

import lombok.Data;

@Data
public class ExchangeVO {

    private Long id;

    private Integer exchangeType;

    private Integer coinType;

    private Long coinValue;

    private Long spuId;

    private Integer periodValue;

    private Integer maxLimit;

    /**
     * 0-可用 1-不可用
     */
    private Integer status;

    private String remark;

}
