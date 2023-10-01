package com.muling.mall.wms.pojo.form.app;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class SwapForm {

    @NotNull(message = "源类型不能为空")
    private Integer srcCoinType;

    @NotNull(message = "目标类型不能为空")
    private Integer dstCoinType;

    @NotNull(message = "转赠数量不能为空")
    private BigDecimal srcCoinValue;

}
