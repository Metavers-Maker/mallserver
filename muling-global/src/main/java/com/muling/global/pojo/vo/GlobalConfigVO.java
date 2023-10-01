package com.muling.global.pojo.vo;


import cn.hutool.json.JSONObject;
import lombok.Data;

@Data
public class GlobalConfigVO {

    private String name;

    private String content;

    private JSONObject ext;

    private Long updated;

}
