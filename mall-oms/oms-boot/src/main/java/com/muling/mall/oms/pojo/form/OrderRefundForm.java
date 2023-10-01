package com.muling.mall.oms.pojo.form;

import lombok.Data;

/**
 * 订单提交表单对象
 *
 * @author huawei
 * @email huawei_code@163.com
 * @date 2021/1/16
 */
@Data
public class OrderRefundForm {

    Long orderId;
    String refundReason;
}
