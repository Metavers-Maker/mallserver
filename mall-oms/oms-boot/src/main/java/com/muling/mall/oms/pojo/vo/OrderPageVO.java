package com.muling.mall.oms.pojo.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 订单分页视图对象
 *
 * @author <a href="mailto:xianrui0365@163.com">haoxr</a>
 * @date 2022/2/1 20:58
 */
@Data
public class OrderPageVO {

    private Long id;
    /**
     * 订单类型 0 1级订单，1 2级订单
     */
    private Integer orderType;
    /**
     * 订单名称
     */
    private String orderName;
    /**
     * 订单号
     */
    private String orderSn;
    /**
     * 订单图片
     */
    private String picUrl;

    /**
     * 总金额
     */
    private Long totalAmount;

    /**
     * 支付总金额
     */
    private Long payAmount;

    /**
     * 手续费
     */
    private Long feeCount;

    /**
     * 支付类型
     */
    private Integer payType;

    private Integer status;

    private Integer totalQuantity;

    private Date created;

    private Long memberId;

    private Integer sourceType;

    private String outTradeNo;

    private List<OrderItem> orderItems;

    @Data
    public static class OrderItem {

        private Long id;

        private Long orderId;

        private Long marketId;

        private Long spuId;

        private String spuName;

        private String productId;

        private String picUrl;

        private Long price;

        private Integer count;

        private Long totalAmount;

    }

}
