package com.muling.mall.ums.pojo.entity;


import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.muling.common.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@TableName(autoResultMap = true)
@Accessors(chain = true)
public class UmsMember extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer gender;

    private String nickName;

    private String uid;

    private String password;

    private String tradePassword;

    private String email;

    private String mobile;

    private String alipayId;

    private String alipay;

    private String openid;

    private String wechat;

    private String avatarUrl;

    private Integer status;

    private Integer authStatus;

    private String secret;

    private Integer isBindGoogle;

    private Integer isOh;

    private String chainAddress;

    private String inviteCode;

//    private Long inviteUser;

    private String deviceId;

    private JSONObject ext;

    private String safeCode;

    private String salt;

    private String lastLoginIp;

    private String lastLoginType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;

    @TableLogic(delval = "1", value = "0")
    private Integer deleted;
}
