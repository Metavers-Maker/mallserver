package com.muling.mall.bms.pojo.vo.app;

import cn.hutool.json.JSONObject;
import lombok.Data;

@Data
public class MemberMissionGroupVO {

    private Long id;
    /**
     * 任务包名称
     */
    private String name;

    /**
     * 用户ID
     */
    private Long memberId;

    /**
     * 任务包ID
     */
    private Long missionGroupId;

    /**
     * 进度0-100
     */
    private Integer percent;

    /**
     * 任务包状态（0进行中，1完成）
     */
    private Integer status;

    /**
     * 奖励状态（0未发放奖励，1已经发放奖励）
     */
    private Integer rewardStatus;

}
