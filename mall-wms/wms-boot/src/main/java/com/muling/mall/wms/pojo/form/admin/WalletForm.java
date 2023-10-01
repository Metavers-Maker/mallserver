package com.muling.mall.wms.pojo.form.admin;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletForm {

    private Long memberId;

    private Integer coinType;

    private BigDecimal balance;

    private String remark;

}
