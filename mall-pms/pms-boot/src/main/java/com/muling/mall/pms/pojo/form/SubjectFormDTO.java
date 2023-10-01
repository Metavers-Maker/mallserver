package com.muling.mall.pms.pojo.form;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Data
@ApiModel("系列表单")
public class SubjectFormDTO {

    private String name;

    private Long brandId;

    private String picUrl;

    private int sort;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private JSONObject ext;

}
