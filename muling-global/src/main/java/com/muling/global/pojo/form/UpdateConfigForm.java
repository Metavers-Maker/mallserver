package com.muling.global.pojo.form;

import cn.hutool.json.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("表单对象")
@Data
public class UpdateConfigForm {

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("类型")
    private String type;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("扩展配置")
    private JSONObject ext;

}



