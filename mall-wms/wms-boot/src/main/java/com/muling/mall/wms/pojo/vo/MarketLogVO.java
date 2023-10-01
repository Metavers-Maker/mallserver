package com.muling.mall.wms.pojo.vo;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MarketLogVO {

    private Long id;

    private Long orderSn;

    private Long memberId;

    private Integer coinType;

    private BigDecimal balance;

    //创建 锁定 取消 提交 确认
    private Integer status;

    private String remark;
}
