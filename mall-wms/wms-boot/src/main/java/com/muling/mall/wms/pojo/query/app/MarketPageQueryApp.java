package com.muling.mall.wms.pojo.query.app;

import com.muling.common.base.BasePageQuery;
import com.muling.mall.wms.common.enums.MarketStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("分页查询对象")
public class MarketPageQueryApp extends BasePageQuery {

    private Integer status;

    private Integer step;

    private String orderBy;

    private boolean asc;
}
