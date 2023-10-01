package com.muling.mall.ums.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.net.URL;

@ApiModel("视图层对象")
@Data
public class STSVO {

    @ApiModelProperty("通道Key")
    private String key;

    @ApiModelProperty("通道密码")
    private String secret;

    @ApiModelProperty("临时Token")
    private String token;

    @ApiModelProperty("过期时间")
    private String expire;

    @ApiModelProperty("上传URL")
    private URL putUrl;

}
