package com.muling.mall.ums.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@RequiredArgsConstructor
public class MemberAuthDTO {

    /**
     * 会员ID
     */
    private Long memberId;
    /**
     * 微信开放Id
     */
    private String openId;
    /**
     * 支付宝开放Id
     */
    private String alipayId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 状态(1:正常；0：禁用)
     */
    private Integer status;
    /**
     * 密码
     */
    private String password;
    private String secret;
    private String salt;
    /**
     * 是否绑定google验证码
     */
    private Integer isBindGoogle;
}
