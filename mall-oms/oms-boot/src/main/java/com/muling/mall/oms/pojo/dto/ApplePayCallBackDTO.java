package com.muling.mall.oms.pojo.dto;

import lombok.Data;

/**
 * 苹果支付回调参数
 */
@Data
public class ApplePayCallBackDTO {

    private String transactionId;

    private String outTradeNo;

    private Long orderId;

    private String receipt;

}
