package com.muling.mall.farm.pojo.entity;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.muling.common.base.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FarmDuomobItem extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

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
     * 是否分红0-未分，1-已分
     * */
    private Integer dispatch;

    /**
     * 内容
     * */
    private String content;

    /**
     * ext 扩展字段
     * */
    private JSONObject extra;

}
