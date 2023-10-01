package com.muling.mall.ums.pojo.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.muling.common.base.BaseEntity;
import com.muling.mall.ums.enums.MemberLogTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@TableName(autoResultMap = true)
@Accessors(chain = true)
public class UmsMemberLog extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long memberId;

    private String clientId;

    private String grantType;

    private MemberLogTypeEnum type;

    private String ip;

    private String deviceId;

    private String deviceName;

    private String deviceVersion;

    private String userAgent;

}
