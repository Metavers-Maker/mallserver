package com.muling.mall.bms.pojo.vo.app;

import lombok.Data;

@Data
public class MarketVO {

    private Long id;

    private Long orderId;

    private Long memberId;

    private Long itemId;

    private Integer itemType;

    private String itemNo;

    private Long subjectId;

    private Long spuId;

    private String name;

    private String picUrl;

    private String sourceUrl;

    private Long price;

    private Long fee;

    private Long buyerId;

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

    private Integer status;

    private Long created;

    /**
     * 额外返回
     * */
    private String memberName;

    private String buyerName;
}
