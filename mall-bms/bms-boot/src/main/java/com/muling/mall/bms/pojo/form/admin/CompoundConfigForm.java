package com.muling.mall.bms.pojo.form.admin;

import com.muling.mall.bms.enums.StatusEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CompoundConfigForm {

    /**
     * 合成目标
     */
    private Long targetId;

    /**
     * 材料ID
     */
    private Long spuId;

    /**
     * 材料数量
     */
    private Integer count;

    /**
     * 消耗积分类型
     */
    private Integer type;

    /**
     * 消耗积分数量
     */
    private BigDecimal typeValue;

    /**
     * 0-可用 1-不可用
     */
    private StatusEnum status;

    private String remark;
}
