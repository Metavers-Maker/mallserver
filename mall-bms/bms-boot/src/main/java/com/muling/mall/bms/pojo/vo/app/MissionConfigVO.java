package com.muling.mall.bms.pojo.vo.app;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class MissionConfigVO {

    private Long id;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 奖励积分类型
     */
    private Integer claimCoinType;

    /**
     * 奖励积分数量
     */
    private BigDecimal claimCoinValue;

    /**
     * 消耗积分类型
     */
    private Integer costCoinType;

    /**
     * 消耗积分数量
     */
    private BigDecimal costCoinValue;

    /**
     * 单个任务的进度值
     */
    private Integer step;

    /**
     * 是否扣除
     */
    private Integer cost;

    /**
     * 剩余任务数量
     */
    private Integer reNum;

    /**
     * 总共派发任务数量
     */
    private Integer mintNum;

    /**
     * 任务可见行
     */
    private Integer visible;

    /**
     * 扩展属性
     */
    private JSONObject ext;

    /**
     * 关联任务包ID
     */
    private Long groupId;

}
