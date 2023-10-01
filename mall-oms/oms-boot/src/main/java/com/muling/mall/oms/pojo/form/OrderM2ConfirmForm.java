package com.muling.mall.oms.pojo.form;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 2级订单提交表单对象
 *
 */

@Data
public class OrderM2ConfirmForm {

    @NotNull(message = "市场ID")
    Long marketId;
}
