package com.muling.mall.otc.pojo.vo;

import lombok.Data;

/**
 * 支付信息表
 */
@Data
public class MarketVO {

    private Long id;

    private Long memberId;


    private Integer status;

    private Long created;
}
