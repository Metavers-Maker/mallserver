package com.muling.mall.bms.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.muling.common.base.BaseEntity;
import com.muling.mall.bms.enums.*;
import com.muling.mall.pms.enums.BindEnum;
import com.muling.mall.pms.enums.InsideEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 用户物品表
 */
@Data
@Accessors(chain = true)
public class OmsMemberItem extends BaseEntity {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long memberId;

    private Long spuId;

    private Integer type;
    /**
     * 商品sku图片
     */
    private String picUrl;

    private String name;

    private String contract;

    private String hexId;

    private String hash;
    /**
     * 物品编号
     */
    private String itemNo;

    private BindEnum bind;

    private InsideEnum inside;
    /**
     * 铸造状态
     */
    private ItemStatusEnum status;

    /**
     * 转移状态
     */
    private ItemTransferEnum transfer;

    /**
     * 冻结状态（未冻结，已冻结）
     */
    private ItemFreezeStatusEnum freeze;

    /**
     * 冻结方式（默认，市场冻结，farm冻结，首发冻结）
     */
    private ItemFreezeTypeEnum freezeType;

    /**
     * 奖励时间
     */
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime started;

    /**
     * 持仓时间
     */
    private Integer stickNum;

    private Long skuId;

    /**
     * 藏品来源类型
     */
    private FromTypeEnum fromType;

    /**
     * 上次变动价格
     */
    private Long swapPrice;

    /**
     * 上链操作的ID
     */
    private String operationId;

    /**
     * 资源链接
     */
    private String sourceUrl;

    /**
     * 资源类型
     */
    private Integer sourceType;

    /**
     * 当前地址
     */
    private String curAddress;
}
