package com.muling.mall.wms.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import com.muling.mall.wms.enums.WalletOpTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class WmsWalletLog extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long memberId;
    private Integer coinType;
    private BigDecimal balance;
    private BigDecimal oldBalance;
    private BigDecimal inBalance;
    private BigDecimal fee;
    private WalletOpTypeEnum opType;

    private String remark;

}
