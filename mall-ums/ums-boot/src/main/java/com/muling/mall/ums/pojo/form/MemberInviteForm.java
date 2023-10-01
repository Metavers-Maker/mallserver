package com.muling.mall.ums.pojo.form;

import lombok.Data;

import java.math.BigDecimal;

/**
 *
 */
@Data
public class MemberInviteForm {

    private Long memberId;

    private Long inviteMemberId;

    private Integer authStatus;

    private String ext;
}
