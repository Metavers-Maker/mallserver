package com.muling.mall.pms.pojo.form;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class BrandForm {

    private Integer role;

    private String name;

    private String picUrl;

    private String simpleDsp;

    private String dsp;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private JSONObject ext;

    private Integer sort;
}
