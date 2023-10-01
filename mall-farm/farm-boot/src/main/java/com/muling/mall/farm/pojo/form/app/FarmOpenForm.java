package com.muling.mall.farm.pojo.form.app;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel("农场开启表单对象")
@Data
public class FarmOpenForm {

    private Long farmId;

    private String validate;

}



