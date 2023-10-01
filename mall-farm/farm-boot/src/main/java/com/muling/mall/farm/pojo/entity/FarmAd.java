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
public class FarmAd extends BaseEntity {

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
     * 奖励积分类型
     * */
    private Integer rewardCoinType;

    /**
     * 奖励积分数量
     * */
    private BigDecimal rewardCoinValue;

    /**
     * 任务阶段
     * */
    private Integer step;

    /**
     * 任务状态
     * */
    private Integer status;

    /**
     * ext 扩展字段
     * */
    private JSONObject ext;

    /**
     * 确认时间
     **/
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ensure;
}
