package com.muling.mall.task.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import com.muling.common.enums.VisibleEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TaskConfig extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Long parentId;

    /**
     * 任务类型：0普通任务，1每日任务
     */
    private Integer taskType;

    /**
     * 奖励积分数量
     */
    private BigDecimal rewardCoinValue;

    /**
     * 奖励积分类型
     */
    private Integer rewardCoinType;

    /**
     * 奖励积分数量
     */
    private String rewardCoinName;

    /**
     * 消耗积分类型
     */
    private Integer costCoinType;

    /**
     * 消耗积分数量
     */
    private BigDecimal costCoinValue;

    /**
     * 消耗积分名称
     */
    private String costCoinName;

    /**
     * 奖励道具ID
     */
    private Long rewardItemId;

    /**
     * 奖励道具名称
     */
    private String rewardItemName;

    /**
     * 奖励道具数量
     */
    private Integer rewardItemCount;

    /**
     * 消耗道具ID
     */
    private Long costItemId;

    /**
     * 消耗道具名称
     */
    private String costItemName;

    /**
     * 消耗道具数量
     */
    private Integer costItemCount;

    /**
     * 奖励道具pic
     */
    private String rewardItemPic;

    /**
     * 消耗道具pic
     */
    private String costItemPic;

    /**
     * 总共派发任务数量
     */
    private Integer totalNum;

    /**
     * 使用任务数量
     */
    private Integer usedNum;

    /**
     * 可见行
     * */
    private VisibleEnum visible;
}
