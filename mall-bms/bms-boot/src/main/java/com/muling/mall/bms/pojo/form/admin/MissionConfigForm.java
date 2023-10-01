package com.muling.mall.bms.pojo.form.admin;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.muling.mall.bms.enums.StatusEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MissionConfigForm {

    private String name;

    private Integer claimCoinType;

    private BigDecimal claimCoinValue;

    private Integer costCoinType;

    private BigDecimal costCoinValue;

    private Integer cost;

    private Integer visible;

    private Integer reNum;

    private Integer mintNum;

    private JSONObject ext;

    private Long groupId;
}
