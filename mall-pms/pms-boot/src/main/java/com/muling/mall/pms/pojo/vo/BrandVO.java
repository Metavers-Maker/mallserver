package com.muling.mall.pms.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("品牌详情")
public class BrandVO {
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("角色")
    private Integer role;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("图片地址")
    private String picUrl;

    @ApiModelProperty("简单描述")
    private String simpleDsp;

    @ApiModelProperty("描述信息")
    private String dsp;

    @ApiModelProperty("额外信息")
    private Object ext;

    @ApiModelProperty("更新时间")
    private Long updated;
}
