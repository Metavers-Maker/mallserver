package com.muling.mall.bms.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import software.amazon.ion.Decimal;

import java.math.BigDecimal;

/**
 * 空投活动任务实体
 * */

@Data
@Accessors(chain = true)
public class BmsAirdropItem extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 活动Id
     * */
    private Long airdropId;

    /**
     * 用户Id
     * */
    private Long memberId;

    /**
     * 绑定SpuId
     * */
    private Long spuId;

    /**
     * Spu数量
     * */
    private Integer spuCount;

    /**
     * 奖励积分类型
     * */
    private Integer coinType;

    /**
     * 奖励积分数量
     * */
    private BigDecimal coinCount;

    /**
     * 执行状态
     * */
    private Integer status;

}
