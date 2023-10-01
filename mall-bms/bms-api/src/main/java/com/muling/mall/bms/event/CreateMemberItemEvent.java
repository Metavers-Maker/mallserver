package com.muling.mall.bms.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateMemberItemEvent implements Serializable {

    /**
     * 用户ID
     */
    private Long memberId;

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 详细信息
     */
    private List<ItemProperty> orderItems;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemProperty implements Serializable {
        /**
         * 订单详情Id
         */
        private Long id;
        /**
         * 商品SpuId
         */
        private Long spuId;
        /**
         * 商品图片地址
         */
        private String picUrl;
        /**
         * 商品名称
         */
        private String spuName;
        /**
         * 订单商品数量
         */
        private Integer count;
    }
}
