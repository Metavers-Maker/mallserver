package com.muling.mall.bms.pojo.vo.app;

import lombok.Data;

@Data
public class CompoundVO {

    private Long id;

    private Long targetId;

    private Long spuId;

    private Long spuCount;

    private Integer type;

    private Long typeValue;
}
