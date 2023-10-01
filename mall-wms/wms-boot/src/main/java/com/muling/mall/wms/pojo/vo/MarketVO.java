package com.muling.mall.wms.pojo.vo;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MarketVO {

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

    private Long buyTimed;

    private Integer step;

    private Integer status;

    private JSONObject ext;

    private Long created;

    private Long updated;
}
