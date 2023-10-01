package com.muling.mall.ums.pojo.form;

import lombok.Data;

import javax.validation.constraints.Size;


@Data
public class InviteCodeForm {

    @Size(min = 5, max = 9, message = "邀请码长度必须在5~9之间")
    private String inviteCode;

}



