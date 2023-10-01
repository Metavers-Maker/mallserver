package com.muling.mall.pms.pojo.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author <a href="mailto:xianrui0365@163.com">haoxr</a>
 * @date 2022/2/5 23:09
 */

@Data
public class SkuInfoDTO {
    /**
     * skuId
     */
    @TableId(value = "id")
    private Long skuId;

    /**
     * spuId
     */
    private Long spuId;

    /**
     * SKU 名称
     */
    private String skuName;
    /**
     * SKU 价格
     */
    private Long price;

    /**
     * 商品图片
     */
    private String picUrl;

    /**
     * 关闭销售
     */
    private Integer closed;

    /**
     * 用于下订单 SKU 库存数量
     */
    private Integer stockNum;

    /**
     * 用于开盲盒的SKU 库存数量
     */
    private Integer rndStockNum;

    /**
     * 总的用于下订单 SKU 库存数量
     */
    private Integer totalStockNum;

    /**
     * 总的用于开盲盒的SKU 库存数量
     */
    private Integer totalRndStockNum;
    /**
     * SPU 名称
     */
    private String spuName;
}
