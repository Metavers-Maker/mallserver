package com.muling.mall.pms.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 系列详情视图对象
 *
 */
@Data
@ApiModel("系列详情")
public class SubjectVO {
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("系列名字")
    private String name;

    @ApiModelProperty("发行方ID")
    private Long brandId;

    @ApiModelProperty("图片地址")
    private String picUrl;

    @ApiModelProperty("额外信息")
    private Object ext;

    @ApiModelProperty("更新时间")
    private Long updated;
}
