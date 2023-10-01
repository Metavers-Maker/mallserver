package com.muling.mall.bms.pojo.query.app;

import com.muling.common.base.BasePageQuery;
import io.swagger.annotations.ApiModel;
import lombok.Data;


@Data
@ApiModel("分页查询对象")
public class MarketPageQueryApp extends BasePageQuery {

    private Long itemId;

    private String name;

    private Long subjectId;

    private Long spuId;

    private Integer itemType;

    private Integer status;

    private String orderBy;

    private boolean asc;
}
