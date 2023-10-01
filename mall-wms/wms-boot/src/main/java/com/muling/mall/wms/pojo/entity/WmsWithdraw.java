package com.muling.mall.wms.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import com.muling.mall.wms.common.enums.CoinStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class WmsWithdraw extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long memberId;
    private BigDecimal balance;
    private Integer status; //0待审核 1通过 2失败 3取消
    private String reason;

}
