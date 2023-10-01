package com.muling.mall.bms.pojo.query.admin;

import com.muling.common.base.BasePageQuery;
import com.muling.mall.bms.enums.FromTypeEnum;
import com.muling.mall.bms.enums.ItemFreezeTypeEnum;
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
public class ItemPageQuery extends BasePageQuery {

    @ApiModelProperty("会员ID")
    private Long memberId;

    @ApiModelProperty("SpuId")
    private Long spuId;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("冻结类型")
    private ItemFreezeTypeEnum freezeType;

    @ApiModelProperty("物品来源")
    private FromTypeEnum fromType;

    @ApiModelProperty("铸造状态")
    private ItemStatusEnum status;
}
