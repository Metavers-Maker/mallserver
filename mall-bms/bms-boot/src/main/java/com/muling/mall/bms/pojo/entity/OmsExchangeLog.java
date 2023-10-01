package com.muling.mall.bms.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 兑换日志表
 */
@Data
@Accessors(chain = true)
public class OmsExchangeLog extends BaseEntity {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long memberId;

    private Integer exchangeType;

    private Integer coinType;

    private BigDecimal coinValue;

    private Long spuId;

    private String itemName;

    private String itemNo;

    private String picUrl;

    private String remark;


}
