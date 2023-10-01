package com.muling.mall.ums.pojo.dto;

import lombok.Data;

@Data
public class MemberRegisterDTO {
    private Long id;
    private String nickName;
    private String inviteCode;
    private String mobile;
}
