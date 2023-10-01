package com.muling.mall.ums.pojo.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.muling.common.base.BaseEntity;
import com.muling.mall.ums.enums.AuthStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@TableName(autoResultMap = true)
@Accessors(chain = true)
public class UmsMemberAuth extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long memberId;

    private String realName;

    private Integer idCardType;

    private String idCard;

    private String mobile;

    private AuthStatusEnum status;

}
