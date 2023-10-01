package com.muling.admin.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Statistics extends BaseEntity {

    @TableId(type= IdType.AUTO)
    private Long id;

    /**
     * 一级市场每天/月 订单数（件）
     */
    private Long orderAmount;

    /**
     * 一级市场每天/月 支付订单数（件）
     */
    private Long orderPayedAmount;

    /**
     * 一级市场每天/月 销售额（元）
     */
    private Long saleAmount;

    /**
     * 二级市场每天/月 订单数（件）
     */
    private Long l2MarketOrderAmount;

    /**
     * 二级市场每天/月 支付订单数（件）
     */
    private Long l2MarketOrderPayedAmount;

    /**
     * 二级市场每天/月 销售额（元）
     */
    private Long l2MarketSaleAmount;

    /**
     * 每天/月 新增用户数（人）
     */
    private Long addedMemberAmount;

    /**
     * 一级市场每天月 下单用户（人）
     */
    private Long orderedMemberAmount;

    /**
     * 二级市场每天月 下单用户（人）
     */
    private Long l2MarketOrderedMemberAmount;

    /**
     * 每天/月 藏品数量（件）
     */
    private Long skuTotalAmount;

    /**
     * 1 每天
     * 2 每月
     * */
    private Integer timeDimension;

    private Long recordTime;


}
