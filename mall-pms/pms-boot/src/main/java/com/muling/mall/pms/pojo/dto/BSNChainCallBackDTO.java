package com.muling.mall.pms.pojo.dto;

import lombok.Data;

/**
 * bsn-chain回调参数
 */
@Data
public class BSNChainCallBackDTO {

    private String transactionId;

    private String outTradeNo;

    private Long orderId;

    private String receipt;

}
