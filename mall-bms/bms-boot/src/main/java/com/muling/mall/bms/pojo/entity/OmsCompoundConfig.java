package com.muling.mall.bms.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import com.muling.mall.bms.enums.StatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 用户商品表
 */
@Data
@Accessors(chain = true)
public class OmsCompoundConfig extends BaseEntity {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 合成目标
     */
    private Long targetId;

    /**
     * 材料ID
     */
    private Long spuId;

    /**
     * 材料数量
     */
    private Integer count;

    /**
     * 消耗积分类型
     */
    private Integer type;

    /**
     * 消耗积分数量
     */
    private BigDecimal typeValue;

    /**
     * 0-可用 1-不可用
     */
    private StatusEnum status;

    private String remark;
}
