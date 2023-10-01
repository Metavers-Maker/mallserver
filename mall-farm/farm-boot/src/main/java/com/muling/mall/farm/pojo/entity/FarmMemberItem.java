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
public class FarmMemberItem extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long farmId;

    private Long memberId;

    private Long itemId;

    private String name;

    private Long spuId;

    //奖励类型
    private Integer claimCoinType;

    //奖励数量
    private BigDecimal claimCoinValue;

    //返佣币种
    private Integer rakeBackCoinType;

    //返佣币种数量
    private BigDecimal rakeBackCoinValue;

    //活跃度当天的值
    private BigDecimal activeValueExt;

    //领取的活跃度
    private BigDecimal claimedActiveValueExt;

    //一次返活跃度
    private BigDecimal rakeActiveOnce;

    //执行次数
    private Integer execTimes;

    //当前周期
    private Integer currPeriod;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime closed;

    //0表示激活，1表示关闭，2表示封禁，3表示冻结
    private Integer status;

}
