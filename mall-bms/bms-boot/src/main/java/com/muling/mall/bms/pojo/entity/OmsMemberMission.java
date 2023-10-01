package com.muling.mall.bms.pojo.entity;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户任务明显
 *
 * @author huawei
 * @email huawei_code@163.com
 * @date 2020-12-30 22:31:10
 */
@Data
@Accessors(chain = true)
public class OmsMemberMission extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long memberId;

    /**
     * 关联任务配置ID
     */
    private Long missionConfigId;

    /**
     * 关联任务包Id
     */
    private Long missionGroupId;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 用户提交的任务内容
     */
    private JSONObject content;


    /**
     * 是否发放完奖励（0未发放，1已发放）
     */
    private Integer reward;

    /**
     * 任务状态
     */
    private Integer status;

}
