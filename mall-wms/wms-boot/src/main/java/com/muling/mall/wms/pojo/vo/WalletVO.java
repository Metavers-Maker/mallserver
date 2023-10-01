package com.muling.mall.wms.pojo.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("视图对象")
public class WalletVO {

    private Long memberId;
    private Integer coinType;
    private BigDecimal balance;
    private Integer status;

}
