package com.muling.mall.wms.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.Version;
import com.muling.common.base.BaseEntity;
import com.muling.mall.wms.common.enums.CoinStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class WmsWallet extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long memberId;
    private Integer coinType;
    private BigDecimal balance;
    private CoinStatusEnum status;

    @Version
    private Integer version;
}
