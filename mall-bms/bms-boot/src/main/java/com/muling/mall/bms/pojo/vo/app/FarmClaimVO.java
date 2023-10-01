package com.muling.mall.bms.pojo.vo.app;

import lombok.Data;

@Data
public class FarmClaimVO {

    private Long id;

    private Long poolId;

    private Long memberId;

    private Integer currentDays;

    private Integer coinType;

    private Long rewardAmount;

    private Integer status;

    private Long created;

}
