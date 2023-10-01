package com.muling.mall.pms.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import com.muling.mall.pms.common.enums.ViewTypeEnum;
import lombok.Data;

/**
 * 商品库存单元实体
 *
 * @author haoxr
 * @date 2022/2/6
 */
@Data
public class PmsSku extends BaseEntity {

    @TableId(type = IdType.NONE)
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
     * 商品图片
     */
    private String picUrl;

    private ViewTypeEnum visible;

    /**
     * 关闭销售
     */
    private Integer closed;


    /**
     * 总的可下单的库存数量,用户用户下单支付购买的sku，只有管理员添加的时候会变，用于展示
     */
    private Integer totalStockNum;

    /**
     * 当前可下单的库存数量,用户用户下单支付购买的sku
     */
    private Integer stockNum;

    /**
     * 锁定的可下单库存数量,用户用户下单支付购买的sku
     */
    private Integer lockedStockNum;

    /**
     * 总的开盲盒的库存数量，只有管理员添加的时候会变，用于展示
     */
    private Integer totalRndStockNum;

    /**
     * 当前开盲盒可的可用库存数量
     */
    private Integer rndStockNum;


}
