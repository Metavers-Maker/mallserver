package com.muling.mall.wms.pojo.form.app;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class TransferForm {

    @NotNull(message = "转赠类型不能为空")
    private Integer coinType;

    @NotNull(message = "转赠数量不能为空")
    private BigDecimal typeValue;

    @NotNull(message = "转赠人不能为空")
    private String toUid;
}
