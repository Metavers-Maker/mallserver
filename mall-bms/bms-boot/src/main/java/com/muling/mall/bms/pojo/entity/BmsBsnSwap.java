package com.muling.mall.bms.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import com.muling.mall.bms.enums.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * Bsn转移记录
 */
@Data
@Accessors(chain = true)
public class BmsBsnSwap extends BaseEntity {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
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
    private String fromAddr;

    /**
     * 目标用户地址
     */
    private String toAddr;

    /**
     * 交易类型(0首发)
     */
    private ChainSwapTypeEnum transType;

    /**
     * 上链转移状态 0 未转移，1转移中 2转移完
     */
    private Integer status;

    /**
     * 上链转移操作ID
     */
    private String operationId;

}
