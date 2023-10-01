package com.muling.mall.pms.pojo.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 商品详情视图对象
 *
 * @author <a href="mailto:xianrui0365@163.com">haoxr</a>
 * @date 2021/8/8
 */
@Data
@ApiModel("商品库存详情")
public class SkuVO {

    private Long id;

    /**
     * SKU 名称
     */
    private String name;

    /**
     * SPU ID
     */
    private Long spuId;


    /**
     * 商品价格(单位：分)
     */
    private Long price;

    /**
     * 库存数量
     */
    private Integer stockNum;

    /**
     * 锁定库存数量
     */
    private Integer lockedStockNum;

    private Long updated;
}
