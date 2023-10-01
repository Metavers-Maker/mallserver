package com.muling.mall.bms.pojo.vo.app;

import com.muling.mall.bms.enums.ChainSwapTypeEnum;
import lombok.Data;

@Data
public class BsnSwapVO {

    /**
     * id
     */
    private Long id;

    /**
     * 物品ID
     */
    private Long itemId;

    /**
     * 物品编号
     */
    private String itemNo;

    /**
     * 商品ID
     */
    private Long spuId;

    /**
     * 商品名称
     */
    private String spuName;

    /**
     * 商品图片
     */
    private String picUrl;

    /**
     * 资源图
     */
    private String sourceUrl;

    /**
     * token Id
     */
    private String tokenId;

    /**
     * 源用户ID
     */
    private Long fromId;

    /**
     * 目标用户ID
     */
    private Long toId;

    /**
     * 源用户地址
     */
    private Long fromAddr;

    /**
     * 目标用户地址
     */
    private Long toAddr;

    /**
     * 交易类型(0首发)
     */
    private ChainSwapTypeEnum transType;

    /**
     * 上链状态
     */
    private Integer status;

}
