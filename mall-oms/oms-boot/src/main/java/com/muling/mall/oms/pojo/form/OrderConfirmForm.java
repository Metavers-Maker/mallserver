package com.muling.mall.oms.pojo.form;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 订单提交表单对象
 *
 */

@Data
public class OrderConfirmForm {
    Long spuId;

    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量不能小于1")
    Integer count = 1;
}
