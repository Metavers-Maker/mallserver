package com.muling.mall.bms.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.muling.common.base.BaseEntity;
import com.muling.mall.bms.enums.ItemTypeEnum;
import com.muling.mall.bms.enums.MarketStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class OmsMarket extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long memberId;

    private Long itemId;

    private ItemTypeEnum itemType;

    private String itemNo;

    private Long subjectId;

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

    /**
     * 订单号
     * */
    private String orderSn;

    /**
     * 链上 NFT-ID
     * */
    private String tokenId;

    /**
     * 链上信息
     * */
    private Long bsnId;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime buyTimed;

    private MarketStatusEnum status;

}
