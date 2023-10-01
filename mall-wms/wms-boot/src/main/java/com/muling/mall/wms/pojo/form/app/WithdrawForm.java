package com.muling.mall.wms.pojo.form.app;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class WithdrawForm {

    @NotNull(message = "源类型不能为空")
    private Integer srcCoinType;

    @NotNull(message = "转赠数量不能为空")
    private BigDecimal srcCoinValue;

}
