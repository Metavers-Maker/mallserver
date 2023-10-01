package com.muling.mall.task.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import lombok.Data;

@Data
public class TaskMember extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 任务配置ID
     * */
    private Long taskId;

    /**
     * 会员ID
     * */
    private Long memberId;

    /**
     * 任务完成次数
     * */
    private Integer times;

    /**
     * 任务名称
     * */
    private String name;

    /**
     * 描述字段
     * */
    private String ext;

    /**
     * 任务状态：0，任务未领取，1任务进行中
     * */
    private Integer status;

}
