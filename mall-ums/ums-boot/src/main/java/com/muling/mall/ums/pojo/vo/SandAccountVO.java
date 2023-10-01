package com.muling.mall.ums.pojo.vo;

import cn.hutool.json.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("杉德会员对象")
@Data
public class SandAccountVO {

    @ApiModelProperty("会员ID")
    private Long id;

    @ApiModelProperty("昵称")
    private String nickName;

}
