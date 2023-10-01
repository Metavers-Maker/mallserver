package com.muling.mall.farm.pojo.entity;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.muling.common.base.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FarmAdItem extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 会员ID
     * */
    private Long memberId;

    /**
     * hiyuan流水号
     * */
    private Long adSn;

    /**
     * 广告类型
     * */
    private Integer adType;

    /**
     * 广告位ID（三方）
     * */
    private String adId;

    /**
     * 交易流水号
     * */
    private String transactionId;

    /**
     * 交易流水号
     * */
    private BigDecimal ecpm;

    /**
     * 回传Count
     * */
    private Integer rewardCount;

    /**
     * 回传Name
     * */
    private String rewardName;

    /**
     * 奖励积分类型
     * */
    private Integer rewardCoinType;

    /**
     * 奖励积分数量
     * */
    private BigDecimal rewardCoinValue;

    /**
     * 奖励积分数量
     * */
    private Integer status;

}
