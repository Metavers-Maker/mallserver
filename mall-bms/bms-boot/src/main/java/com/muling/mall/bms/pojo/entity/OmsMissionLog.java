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
public class OmsMissionLog extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 任务名称
     */
    private Long memberId;

    /**
     * 任务包Id
     */
    private Long missionGroupId;

    /**
     * 任务Id
     */
    private Long missionId;

    /**
     * 日志类型
     */
    private Integer logType;

    /**
     * 日志描述
     */
    private String logDsp;

}
