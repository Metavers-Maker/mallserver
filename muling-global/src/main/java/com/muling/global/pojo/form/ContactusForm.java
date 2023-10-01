package com.muling.global.pojo.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@ApiModel("表单对象")
@Data
public class ContactusForm {

    @NotNull(message = "姓名不能为空")
    @Size(min = 2, max = 20, message = "姓名长度必须在2~20之间")
    private String name;

    @NotNull(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotNull(message = "标题不能为空")
    @Size(max = 128, message = "标题长度不能超过128")
    private String title;

    @NotNull(message = "内容不能为空")
    @Size(min = 2, max = 500, message = "内容长度必须在2~500之间")
    private String content;


}



