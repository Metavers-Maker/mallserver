package com.muling.mall.ums.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("视图层对象")
@Data
public class MemberWhiteVO {

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("白名单等级")
    private Integer level;

}
