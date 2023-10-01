package com.muling.mall.bms.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.muling.mall.bms.enums.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MarketItemDTO {

    private Long id;

    private Long memberId;

    private Long itemId;

    private ItemTypeEnum itemType;

    private String itemNo;

    private Long spuId;

    private String name;

    private String picUrl;

    private String sourceUrl;

    private Long price;

    private Integer coinType;

    private BigDecimal coinNum;

    private BigDecimal fee;

    private Long buyerId;

    private String buyerName;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime buyTimed;

    private MarketStatusEnum status;
}
