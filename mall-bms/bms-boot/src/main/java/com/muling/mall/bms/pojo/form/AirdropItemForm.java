package com.muling.mall.bms.pojo.form;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AirdropItemForm {

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
