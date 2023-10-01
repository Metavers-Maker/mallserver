package com.muling.mall.bms.pojo.form.admin;

import com.muling.mall.bms.enums.StatusEnum;
import lombok.Data;

@Data
public class StakeConfigForm {

    private String name;

    private String data;

    private Long spuId;

    private Long balance;

    private Double totalAllocPoint;

    private String remark;

    /**
     * 0-可用 1-不可用
     */
    private StatusEnum status;
}
