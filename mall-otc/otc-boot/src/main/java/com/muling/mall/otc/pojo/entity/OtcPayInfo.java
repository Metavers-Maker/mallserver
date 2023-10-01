package com.muling.mall.otc.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import com.muling.mall.oms.enums.StatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 支付信息表
 */
@Data
@Accessors(chain = true)
public class OtcPayInfo extends BaseEntity {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long memberId;

    private Integer payType;

    private String name;

    private String qrCode;

    private String remark;

    private StatusEnum status;

}
