package com.muling.mall.ums.pojo.dto;

import com.muling.mall.ums.enums.FollowStatusEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AdMissionDispatchDTO {
    private Long memberId;
    private Integer coinType;
    private BigDecimal coinValue;
    private BigDecimal directValue;
    private BigDecimal adLevel1Value;
    private BigDecimal adLevel2Value;
    private BigDecimal adLevel3Value;
    private BigDecimal adLevel4Value;
}
