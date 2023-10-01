package com.muling.mall.oms.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import com.muling.mall.oms.enums.PayChannelStatusEnum;
import com.muling.mall.oms.enums.PayTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 支付渠道表
 */
@Data
@Accessors(chain = true)
public class OmsPayChannel extends BaseEntity {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private PayTypeEnum payType;

    private PayChannelStatusEnum status;
}
