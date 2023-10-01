package com.muling.mall.ums.pojo.form.admin;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Size;

@ApiModel("添加邀请码表单对象")
@Data
public class InviteCodeForm {

    @Size(min = 5, max = 9, message = "邀请码长度必须在5~9之间")
    private String inviteCode;

    private Long memberId;
}



