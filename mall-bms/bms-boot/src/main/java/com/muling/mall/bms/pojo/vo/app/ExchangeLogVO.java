package com.muling.mall.bms.pojo.vo.app;

import lombok.Data;

@Data
public class ExchangeLogVO {

    private Long memberId;

    private Integer exchangeType;

    private Integer coinType;

    private Long coinValue;

    private Long spuId;

    private String itemName;

    private String itemNo;

    private String picUrl;

    private String remark;

    private Long created;

}
