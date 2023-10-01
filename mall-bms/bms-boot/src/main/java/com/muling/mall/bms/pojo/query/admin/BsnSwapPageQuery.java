package com.muling.mall.bms.pojo.query.admin;

import com.muling.common.base.BasePageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * freeze
 */

@Data
@ApiModel("Bsn链上转移分页查询对象")
public class BsnSwapPageQuery extends BasePageQuery {

    @ApiModelProperty("会员ID")
    private Long memberId;

    @ApiModelProperty("SpuId")
    private Long spuId;

}
