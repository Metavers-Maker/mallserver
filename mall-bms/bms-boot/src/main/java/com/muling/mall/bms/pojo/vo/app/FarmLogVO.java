package com.muling.mall.bms.pojo.vo.app;

import lombok.Data;

@Data
public class FarmLogVO {

    private Long id;

    private Long poolId;

    private Long memberId;

    private Long itemId;

    private Long spuId;

    private String itemName;

    private String itemNo;

    private String picUrl;

    private Double allocPoint;

    private Integer days;

    private Integer currentDays;

    private Integer logType;

}
