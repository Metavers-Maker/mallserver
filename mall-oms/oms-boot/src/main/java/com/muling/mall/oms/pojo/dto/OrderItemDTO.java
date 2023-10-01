package com.muling.mall.oms.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 订单商品明细
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class OrderItemDTO implements Serializable {

    /**
     * 二级市场的ID
     */
    @ApiModelProperty("二级市场ID")
    private Long marketId;

    /**
     * 商品图片地址
     */
    @ApiModelProperty("商品图片地址")
    private String picUrl;

    /**
     * 商品价格
     */
    @ApiModelProperty("商品价格")
    private Long price;

    /**
     * 商品ID
     */
    @ApiModelProperty("商品库存单元ID")
    private Long spuId;

    /**
     * 商品名称
     */
    @ApiModelProperty("商品名称")
    private String spuName;

    /**
     * 订单商品数量
     */
    @ApiModelProperty("订单商品数量")
    private Integer count;

}
