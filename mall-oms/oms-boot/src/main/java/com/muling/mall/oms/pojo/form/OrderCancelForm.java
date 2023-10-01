package com.muling.mall.oms.pojo.form;

import lombok.Data;

/**
 * 订单提交表单对象
 */
@Data
public class OrderCancelForm {

    private Long orderId;

    private String reason;

}
