package com.muling.mall.otc.pojo.vo;

import lombok.Data;

/**
 * 支付信息表
 */
@Data
public class PayInfoVO {

    private Long id;

    private Long memberId;

    private Integer payType;

    private String name;

    private String qrCode;

    private String remark;

    private Integer status;

    private Long created;
}
