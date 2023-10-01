package com.muling.mall.bms.pojo.form.admin;

import com.muling.mall.bms.enums.ExchangeTypeEnum;
import com.muling.mall.bms.enums.StatusEnum;
import lombok.Data;

@Data
public class ExchangeConfigForm {


    private ExchangeTypeEnum exchangeType;

    private Integer coinType;

    private Long spuId;

    private Long coinValue;

    private Integer periodType;

    private Integer periodValue;

    /**
     * 0-可用 1-不可用
     */
    private StatusEnum status;

    private String remark;

}
