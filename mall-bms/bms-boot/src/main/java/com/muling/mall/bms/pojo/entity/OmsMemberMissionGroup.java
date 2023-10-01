package com.muling.mall.bms.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户任务组
 *
 * @author huawei
 * @email huawei_code@163.com
 * @date 2020-12-30 22:31:10
 */
@Data
@Accessors(chain = true)
public class OmsMemberMissionGroup extends BaseEntity {

    @TableId(type = IdType.AUTO)
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
