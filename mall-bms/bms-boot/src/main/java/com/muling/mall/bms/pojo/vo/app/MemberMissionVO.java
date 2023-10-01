package com.muling.mall.bms.pojo.vo.app;

import cn.hutool.json.JSONObject;
import lombok.Data;


@Data
public class MemberMissionVO {

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
     * 任务状态
     */
    private Integer status;

}
