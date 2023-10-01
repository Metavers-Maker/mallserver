package com.muling.mall.wms.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class WmsTransferLog extends BaseEntity {


    @TableId(type = IdType.AUTO)
    private Long id;

    private Long sourceId;

    private Long targetId;

    private String targetUid;

    private Integer coinType;

    private BigDecimal balance;

    private BigDecimal fee;

    private String remark;

}
