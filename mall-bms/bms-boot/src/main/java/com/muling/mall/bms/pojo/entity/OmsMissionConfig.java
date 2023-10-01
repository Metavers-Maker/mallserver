package com.muling.mall.bms.pojo.entity;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 任务配置
 *
 * @author huawei
 * @email huawei_code@163.com
 * @date 2020-12-30 22:31:10
 */
@Data
@Accessors(chain = true)
public class OmsMissionConfig extends BaseEntity {

    @TableId(type = IdType.AUTO)
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
