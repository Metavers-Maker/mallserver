package com.muling.mall.wms.pojo.entity;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.muling.common.base.BaseEntity;
import com.muling.mall.wms.common.enums.MarketStatusEnum;
import com.muling.mall.wms.common.enums.MarketStepEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class WmsMarket extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderSn;

    private Long memberId;

    private String memberName;

    private Integer opType;

    private Integer coinType;

    private BigDecimal balance;

    private BigDecimal fee;

    private BigDecimal singlePrice;

    private BigDecimal totalPrice;

    private Long buyerId;

    private String buyerName;

    private MarketStepEnum step;

    private MarketStatusEnum status;

    private Integer dispatch;

    /**
     * 扩展属性
     * */
    private JSONObject ext;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime buyTimed;
}
