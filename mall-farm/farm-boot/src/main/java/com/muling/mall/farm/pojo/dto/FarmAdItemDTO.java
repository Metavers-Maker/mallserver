package com.muling.mall.farm.pojo.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FarmAdItemDTO {

    private Long memberId;

    /**
     * 流水号
     * */
    private Long adSn;

    /**
     * 广告类型
     * */
    private Integer adType;

    /**
     * 广告位
     * */
    private String adId;

    /**
     * 唯一交易ID
     * */
    private String transId;

    /**
     * 回传Ecpm
     * */
    private BigDecimal ecpm;

    /**
     * 回传Count
     * */
    private Integer rewardCount;

    /**
     * 回传Name
     * */
    private String rewardName;

}
