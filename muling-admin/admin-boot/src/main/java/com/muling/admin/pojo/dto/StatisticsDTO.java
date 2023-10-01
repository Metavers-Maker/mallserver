package com.muling.admin.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author chen
 */
@Data
@Accessors(chain = true)
@ApiModel("总的统计查询结果")
public class StatisticsDTO {

    /**
     * 藏品总量（件）
     */
    @ApiModelProperty("藏品总量（件）")
    private Long totalGoodsAmount;

    /**
     * 一级市场订单总量（件）
     */
    @ApiModelProperty("一级市场订单总量（件）")
    private Long totalOrderAmount;


    /**
     * 二级市场订单总量（件）
     */
    @ApiModelProperty("二级市场订单总量（件）")
    private Long l2MarketTotalOrderAmount;


    /**
     * 用户总量（人）
     */
    @ApiModelProperty("用户总量（人）")
    private Long totalMemberAmount;

    /**
     * 一级市场销售总额（元）
     */
    @ApiModelProperty("一级市场销售总额（分）")
    private Long totalSaleAmount;

    /**
     * 二级市场销售总额（元）
     */
    @ApiModelProperty("二级市场销售总额（分）")
    private Long l2MarketTotalSaleAmount;


    private List<StatisticsDetail> list;

}
