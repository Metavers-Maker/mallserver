package com.muling.mall.ums.pojo.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.muling.common.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@TableName(autoResultMap = true)
@Accessors(chain = true)
public class UmsMemberAuthLog extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long memberId;

    private String request;

    private String response;

    private String seqNo;
}
