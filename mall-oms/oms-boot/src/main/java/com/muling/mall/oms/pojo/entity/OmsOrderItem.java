package com.muling.mall.oms.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 订单明细表
 *
 * @author huawei
 * @email huawei_code@163.com
 * @date 2020-12-30 22:31:10
 */
@Data
@Accessors(chain = true)
public class OmsOrderItem extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 二级市场的Id
     */
    private Long marketId;

    /**
     * 物品编号
     */
    private String itemNo;

    /**
     * 商品名称
     */
    private String spuName;

    /**
     * 商品ID
     */
    private Long spuId;

    /**
     * 库存规格ID
     */
    private Long skuId;

    /**
     * 规格名称
     */
    private String skuName;

    /**
     * 商品详情图片(资源图)
     */
    private String picUrl;

    /**
     * 商品单价(单位：分)
     */
    private Long price;

    /**
     * 苹果支付ID
     */
    private String productId;

    /**
     * 商品类型（NFT，盲盒...）
     */
    private Integer type;

    /**
     * 商品数量
     */
    private Integer count;

    /**
     * 商品总金额(单位：分)
     */
    private Long totalAmount;

    /**
     * 逻辑删除(0:正常；1:已删除)
     */
    private Integer deleted;

}
