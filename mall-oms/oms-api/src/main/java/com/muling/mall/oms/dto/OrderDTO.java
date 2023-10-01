package com.muling.mall.oms.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单分页视图对象
 *
 * @author <a href="mailto:xianrui0365@163.com">haoxr</a>
 * @date 2022/2/1 20:58
 */
@Data
public class OrderDTO {

    private Long id;

    private String orderSn;

    private Long totalAmount;

    private Long payAmount;

    private Integer payType;

    private Integer status;

    private Integer totalQuantity;

    private LocalDateTime created;

    private Long memberId;

    private Integer sourceType;

    private String outTradeNo;

    private Integer orderType;
}
