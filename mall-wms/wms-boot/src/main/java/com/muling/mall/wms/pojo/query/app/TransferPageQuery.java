package com.muling.mall.wms.pojo.query.app;

import com.muling.common.base.BasePageQuery;
import io.swagger.annotations.ApiModel;
import lombok.Data;


@Data
@ApiModel("分页查询对象")
public class TransferPageQuery extends BasePageQuery {

    private Integer coinType;
}
