package com.muling.mall.wms.pojo.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("钱包市场日志视图对象")
public class WmsMarketLogVO {

    /**
     * 订单ID
     * */
    private Long marketId;

    /**
     * 用户ID
     * */
    private Long memberId;

    /**
     * 积分类型
     * */
    private Integer coinType;

    /**
     * 积分数量
     * */
    private BigDecimal balance;

    /**
     * 状态
     * */
    private Integer status;

    /**
     * 备注
     * */
    private String remark;

    private Long created;

    private Long updated;

}
