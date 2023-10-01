package com.muling.mall.pms.pojo.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class SkuForm {

    @ApiModelProperty("spuId")
    private Long spuId;
    /**
     * SKU 名称
     */
    @ApiModelProperty("SKU 名称")
    private String name;

    /**
     * 商品价格(单位：分)
     */
    @ApiModelProperty("商品价格(单位：分)")
    private Long price;

    /**
     * 关闭销售
     */
    @ApiModelProperty("closed")
    private Integer closed;

    /**
     * 商品主图
     */
    @ApiModelProperty("商品主图")
    private String picUrl;

    /**
     * 用于下订单的sku库存数量
     */
    @ApiModelProperty("用于下订单的sku库存数量")
    private Integer stockNum;

    /**
     * 用于开盲盒用的sku库存数量
     */
    @ApiModelProperty("用于开盲盒的sku库存数量")
    private Integer rndStockNum;
}
