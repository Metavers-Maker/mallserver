package com.muling.global.pojo.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("知识库表单对象")
@Data
public class NewsForm {

    @ApiModelProperty("类型")
    private Integer type;

    @ApiModelProperty("名称")
    private String title;

    @ApiModelProperty("类型")
    private String ext;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("排序")
    private Integer sort;

}



