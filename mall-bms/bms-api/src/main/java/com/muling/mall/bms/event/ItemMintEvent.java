package com.muling.mall.bms.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemMintEvent implements Serializable {

    /**
     * 用户ID
     */
    private Long memberId;

    /**
     * 商品ID
     */
    private Long spuId;

    /**
     * 物品编号
     */
    private String itemNo;
}
