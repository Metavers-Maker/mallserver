package com.muling.mall.task.pojo.form.app;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel("提交任务条目配置表单对象")
@Data
public class SubmitTaskItemForm {

    private Long taskId;

    private String ext;
}



