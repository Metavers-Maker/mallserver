package com.muling.mall.farm.pojo.form.admin;

import cn.hutool.json.JSONObject;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;

@ApiModel("农场配置表单对象")
@Data
public class FarmDuomobForm {

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
     * 生成时间戳
     * */
    private Integer genTime;

    /**
     * 内容
     * */
    private String content;

    /**
     * ext 扩展字段
     * */
    private JSONObject extra;

}



