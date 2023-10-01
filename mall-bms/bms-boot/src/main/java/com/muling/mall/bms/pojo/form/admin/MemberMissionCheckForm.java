package com.muling.mall.bms.pojo.form.admin;

import lombok.Data;

@Data
public class MemberMissionCheckForm {

    /**
     * 任务状态 0 未提交，1未审核，2审核通过，3审核失败
     */
    private Integer status;
}
