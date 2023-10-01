package com.muling.mall.farm.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.muling.common.base.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FarmMember extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 农场id
     * */
    private Long farmId;

    /**
     * 会员id
     * */
    private Long memberId;

    /**
     * 奖励货币类型
     * */
    private Integer claimCoinType;

    /**
     * 奖励货币数量
     * */
    private BigDecimal claimCoinValue;

    /**
     * 活跃度每天的值
     * */
    private BigDecimal activeValueExt;

    /**
     * 领取的活跃度
     * */
    private BigDecimal claimedActiveValueExt;

    /**
     * 返佣货币数量
     * */
    private Integer rakeBackCoinType;

    /**
     * 返佣货币类型
     * */
    private BigDecimal rakeBackCoinValue;

    /**
     * 允许领取的时间
     * */
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime allowClaimed;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime claimed;

    /**
     * 状态 0 正常，1关闭
     * */
    private Integer status;

    /**
     * 烧伤码
     * */
    private Integer burnCode;

}
