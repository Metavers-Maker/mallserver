package com.muling.mall.bms.pojo.query.app;

import com.muling.common.base.BasePageQuery;
import io.swagger.annotations.ApiModel;
import lombok.Data;


/**
 * @author <a href="mailto:xianrui0365@163.com">haoxr</a>
 * @date 2022/2/1 19:14
 */
@Data
@ApiModel("分页查询对象")
public class FarmClaimPageQuery extends BasePageQuery {


    private Long poolId;

}
