package com.muling.mall.otc.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.muling.common.base.BaseEntity;
import com.muling.mall.oms.enums.ItemTypeEnum;
import com.muling.mall.oms.enums.MarketStatusEnum;
import com.muling.mall.otc.common.enums.MarketStepEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class OtcMarket extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private Long memberId;

    private Long itemId;

    private ItemTypeEnum itemType;

    private String itemNo;

    private Long spuId;

    private String name;

    private String picUrl;

    //总额(分)
    private Long amount;

    private Integer feeType;

    private Long fee;

    private Long buyerId;

    private String buyerName;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime buyTimed;

    private MarketStepEnum step;

    private MarketStatusEnum status;

}
