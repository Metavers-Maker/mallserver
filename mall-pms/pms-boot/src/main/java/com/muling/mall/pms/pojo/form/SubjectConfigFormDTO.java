package com.muling.mall.pms.pojo.form;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("系列配置表单")
public class SubjectConfigFormDTO {
    /**
     * 系列Id
     * */
    private Long subjectId;
    /**
     * 商品Id
     * */
    private Long spuId;
    /**
     * 排序
     * */
    private Integer sort;
}
