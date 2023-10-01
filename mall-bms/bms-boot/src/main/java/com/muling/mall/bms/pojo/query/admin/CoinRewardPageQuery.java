package com.muling.mall.bms.pojo.query.admin;

import com.muling.common.base.BasePageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("分页查询对象")
public class CoinRewardPageQuery extends BasePageQuery {

    @ApiModelProperty("name")
    private String name;

    @ApiModelProperty("fromType")
    private Integer fromType;
}
