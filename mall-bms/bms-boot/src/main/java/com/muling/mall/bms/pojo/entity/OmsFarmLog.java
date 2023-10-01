package com.muling.mall.bms.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import com.muling.mall.bms.enums.StakeItemLogTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 锁仓日志
 */
@Data
@Accessors(chain = true)
public class OmsFarmLog extends BaseEntity {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    private StakeItemLogTypeEnum logType;

    private Long poolId;

    private Long memberId;

    private Long itemId;

    private Long spuId;

    private String itemName;

    private String itemNo;

    private String picUrl;

    private Double allocPoint;

    private Integer days;

    private Integer currentDays;

}
