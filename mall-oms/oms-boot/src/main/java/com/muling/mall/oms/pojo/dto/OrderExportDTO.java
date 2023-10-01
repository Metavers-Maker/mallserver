package com.muling.mall.oms.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OrderExportDTO {
    private String spuName;

    private Long spuId;


    /**
     * 订单号
     */
    private String orderSn;

    private Long memberId;

    private String memberMobile;


}
