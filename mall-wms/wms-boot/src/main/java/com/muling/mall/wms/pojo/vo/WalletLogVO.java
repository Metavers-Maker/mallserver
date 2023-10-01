package com.muling.mall.wms.pojo.vo;

import com.muling.mall.wms.enums.WalletOpTypeEnum;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("钱包日志视图对象")
public class WalletLogVO {

    private Long memberId;
    private Integer coinType;
    private BigDecimal inBalance;
    private BigDecimal fee;
    private Integer opType;

    private String remark;

    private Long created;

}
