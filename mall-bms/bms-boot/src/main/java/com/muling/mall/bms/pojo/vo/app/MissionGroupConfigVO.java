package com.muling.mall.bms.pojo.vo.app;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class MissionGroupConfigVO {

    private Long id;
    /**
     * 任务包名称
     */
    private String name;

    /**
     * 奖励积分数量
     */
    private BigDecimal rewardCoinValue;

    /**
     * 奖励积分类型
     */
    private Integer rewardCoinType;

    /**
     * 任务包可见行
     */
    private Integer visible;

    /**
     * 扩展属性
     */
    private JSONObject ext;

}
