package com.muling.mall.wms.pojo.form.app;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class NewSwapForm {

    @NotNull(message = "兑换ID不能为空")
    private Long swapId;

    @NotNull(message = "数量不能为空")
    private BigDecimal sourceCoinValue;

}
