package com.muling.mall.bms.pojo.query.admin;

import com.muling.common.base.BasePageQuery;
import io.swagger.annotations.ApiModel;
import lombok.Data;


@Data
@ApiModel("分页查询对象")
public class StakePageQuery extends BasePageQuery {


    private Long spuId;

}
