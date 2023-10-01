package com.muling.mall.wms.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class WmsMarketLog extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderSn;

    private Long memberId;

    private Integer coinType;

    private BigDecimal balance;

    /**
     * 0:
     * 1: 创建买单
     * 2: 订单更新
     * 3: 订单锁定
     * 4: 订单取消
     * 5: 买家订单确认
     * 6: 卖家订单确认
     * 7: 订单销毁
     *
     * */
    private Integer status;

    private String remark;

}
