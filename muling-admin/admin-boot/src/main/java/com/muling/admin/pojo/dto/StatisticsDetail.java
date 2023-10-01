package com.muling.admin.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel("每天统计结果")
public class StatisticsDetail {

    /**
     * 订单数（件）
     */
    @ApiModelProperty("一级市场订单数（件）")
    private Long orderAmount;

    /**
     * 支付订单数（件）
     */
    @ApiModelProperty("一级市场支付订单数（件）")
    private Long orderPayedAmount;

    /**
     * 销售额（分）
     */
    @ApiModelProperty("一级市场销售额（分）")
    private Long saleAmount;

    /**
     * 新增用户数（人）
     */
    @ApiModelProperty("新增用户数（人）")
    private Long addedMemberAmount;

    /**
     * 下单用户（人）
     */
    @ApiModelProperty("一级市场下单用户（人）")
    private Long orderedMemberAmount;

    /**
     * 每天/月 藏品数量（件）
     */
    @ApiModelProperty("每天/月 藏品数量（件）")
    private Long skuTotalAmount;

    /**
     * 时间戳，确定到日期即可
     */
    @ApiModelProperty("时间戳，确定到日期即可")
    private Long timestamp;

    /**
     * 时间字符串
     */
    @ApiModelProperty("时间字符串")
    private String timeStr;


    /**
     * 二级市场每天/月 订单数（件）
     */
    @ApiModelProperty("二级市场每天/月 订单数（件）")

    private Long l2MarketOrderAmount;

    /**
     * 二级市场每天/月 支付订单数（件）
     */
    @ApiModelProperty("二级市场每天/月 支付订单数（件）")
    private Long l2MarketOrderPayedAmount;

    /**
     * 二级市场每天/月 销售额（元）
     */
    @ApiModelProperty("二级市场每天/月 销售额（分）")
    private Long l2MarketSaleAmount;

    /**
     * 二级市场每天月 下单用户（人）
     */
    @ApiModelProperty("二级市场每天月 下单用户（人）")
    private Long l2MarketOrderedMemberAmount;
}