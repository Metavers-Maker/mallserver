package com.muling.mall.wms.pojo.query.app;

import com.muling.common.base.BasePageQuery;
import io.swagger.annotations.ApiModel;
import lombok.Data;


@Data
@ApiModel("分页查询对象")
public class MarketBuyPageQueryApp extends BasePageQuery {
    //
    private Integer status;
    //
    private Integer step;
    //
    private String orderBy;
    //
    private boolean asc;
}
