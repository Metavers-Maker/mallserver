package com.muling.mall.bms.pojo.entity;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 会员任务包配置
 *
 * @author huawei
 * @email huawei_code@163.com
 * @date 2020-12-30 22:31:10
 */
@Data
@Accessors(chain = true)
public class OmsMissionGroupConfig extends BaseEntity {

    @TableId(type = IdType.AUTO)
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
