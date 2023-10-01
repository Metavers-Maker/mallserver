package com.muling.mall.farm.pojo.dto;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FarmDuomobItemDTO {
    /**
     * 会员ID
     * */
    private Long memberId;

    /**
     * 流水号
     * */
    private String orderId;

    /**
     * 广告ID
     * */
    private Integer advertId;

    /**
     * 广告名
     * */
    private String advertName;

    /**
     * 媒体奖励
     * */
    private BigDecimal mediaIncome;

    /**
     * 用户奖励
     * */
    private BigDecimal memberIncome;

    /**
     * 媒体Id
     * */
    private String mediaId;

    /**
     * 设备Id
     * */
    private String deviceId;

    /**
     * 内容
     * */
    private String content;

    /**
     * 内容
     * */
    private Integer genTime;

    /**
     * ext 扩展字段
     * */
    private JSONObject extra;
}
