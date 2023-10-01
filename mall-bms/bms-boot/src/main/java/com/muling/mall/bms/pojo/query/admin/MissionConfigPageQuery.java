package com.muling.mall.bms.pojo.query.admin;

import com.muling.common.base.BasePageQuery;
import com.muling.mall.bms.enums.ItemStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author <a href="mailto:xianrui0365@163.com">haoxr</a>
 * @date 2022/2/1 19:14
 */
@Data
@ApiModel("分页查询对象")
public class MissionConfigPageQuery extends BasePageQuery {

    @ApiModelProperty("名称")
    private String name;

}
