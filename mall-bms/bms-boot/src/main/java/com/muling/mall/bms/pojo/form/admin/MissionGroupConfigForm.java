package com.muling.mall.bms.pojo.form.admin;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.muling.mall.bms.enums.StatusEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MissionGroupConfigForm {

    private String name;

    private Integer rewardCoinType;

    private BigDecimal rewardCoinValue;

    private Integer visible;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private JSONObject ext;
}
