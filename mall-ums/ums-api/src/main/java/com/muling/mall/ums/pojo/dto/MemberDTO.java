package com.muling.mall.ums.pojo.dto;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MemberDTO {

    private Long id;

    private Integer gender;

    private String nickName;

    private String alipayId;

    private String alipay;

    private String openid;

    private String wechat;

    private String chainAddress;

    private String mobile;

    private String email;

    private LocalDate birthday;

    private String avatarUrl;

    private String inviteCode;

    /**
     * 0 未实名
     * 3 已实名
     */
    private Integer authStatus;

    /**
     * 0 系统封禁
     * 1 正常
     */
    private Integer status;

    /**
     * 0 未注销
     * 1 注销
     */
    private Integer deleted;

    /**
     * 扩展信息
     */
    private JSONObject ext;
}
