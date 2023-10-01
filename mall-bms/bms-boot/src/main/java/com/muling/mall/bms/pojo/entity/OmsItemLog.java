package com.muling.mall.bms.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import com.muling.mall.bms.enums.ItemLogTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户物品日志表
 */
@Data
@Accessors(chain = true)
public class OmsItemLog extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private ItemLogTypeEnum type;

    private String itemName;

    private String itemNo;

    private Long price;

    private Long memberId;

    private Long memberFrom;

    private Long memberTo;

    private Long spuId;

    private String picUrl;

    private String sourceUrl;

    private String reason;

    private Long skuId;
}
