package com.muling.mall.ums.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import lombok.Data;

@Data
public class UmsAccountChain extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long memberId;

    private String address;

    private String prikey;

    private Integer chainType;

    private String bankCardCode;

    private String bankName;

    private String bankUsername;

    private Integer status;

}
