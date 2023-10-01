package com.muling.mall.bms.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


/**
 * 转移用户物品事件
 * */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransMemberItemEvent implements Serializable {

    /**
     * 目标用户
     */
    private Long memberId;

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 物品详细信息
     */
    private List<ItemProperty> orderItems;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemProperty implements Serializable {
        private Long marketId;
    }
    //
}
