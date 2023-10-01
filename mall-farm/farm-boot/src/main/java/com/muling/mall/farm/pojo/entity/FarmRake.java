package com.muling.mall.farm.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FarmRake extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 会员ID
     * */
    private Long memberId;

    /**
     * 目标ID
     * */
    private Long targetId;

    /**
     * 奖励积分类型
     * */
    private Integer coinType;

    /**
     * 奖励积分数量
     * */
    private BigDecimal coinValue;

    /**
     * 状态 0 为收获，1已收获
     * */
    private Integer status;
}
