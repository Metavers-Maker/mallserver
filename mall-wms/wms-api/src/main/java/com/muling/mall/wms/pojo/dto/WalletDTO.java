package com.muling.mall.wms.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 传输层实体
 */
@Data
@Accessors(chain = true)
public class WalletDTO {

    private Long memberId;
    private Integer coinType;
    //数量
    private BigDecimal balance;
    //费用
    private BigDecimal fee = BigDecimal.ZERO;

    private Integer opType;
    private String remark;
}
