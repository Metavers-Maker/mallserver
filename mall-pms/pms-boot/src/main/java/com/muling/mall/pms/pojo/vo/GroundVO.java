package com.muling.mall.pms.pojo.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("广场详情")
public class GroundVO {

    private Long id;

    private String name;

    private Integer type;

    private Long spuId;

    private Long updated;
}
