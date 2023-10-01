package com.muling.mall.bms.pojo.form.app;

import cn.hutool.json.JSONObject;
import lombok.Data;

@Data
public class MemberMissionForm {

    /**
     * 任务名称
     */
    private String name;

    /**
     * 任务内容
     */
    private JSONObject content;
}
