package com.muling.mall.ums.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import lombok.Data;

@Data
public class UmsSandAccount extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long memberId;

    private String userId;

    private String nickName;

    private String orderSn;

    /**
     * 0 未生效 1生效
     * */
    private Integer status;

}
