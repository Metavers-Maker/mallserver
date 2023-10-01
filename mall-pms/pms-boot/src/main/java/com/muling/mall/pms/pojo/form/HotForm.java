package com.muling.mall.pms.pojo.form;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.muling.mall.pms.common.enums.ContentTypeEnum;
import lombok.Data;

@Data
public class HotForm {

    private String name;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private JSONObject ext;

    private ContentTypeEnum contentType;

    private Long contentId;

}
