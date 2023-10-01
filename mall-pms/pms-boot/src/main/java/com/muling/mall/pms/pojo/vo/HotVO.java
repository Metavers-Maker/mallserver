package com.muling.mall.pms.pojo.vo;

import cn.hutool.json.JSONObject;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("热度视图对象")
public class HotVO {

    private String name;

    private Object ext;

    private Integer contentType;
    private Long contentId;

}
