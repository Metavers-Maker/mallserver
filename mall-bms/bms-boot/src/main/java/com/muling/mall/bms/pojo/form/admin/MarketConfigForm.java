package com.muling.mall.bms.pojo.form.admin;

import com.muling.mall.bms.enums.StatusEnum;
import lombok.Data;
import software.amazon.ion.Decimal;

import java.math.BigDecimal;

@Data
public class MarketConfigForm {
    private String name;

    private Long spuId;

    private Integer coinType;

    private BigDecimal fee;
}
