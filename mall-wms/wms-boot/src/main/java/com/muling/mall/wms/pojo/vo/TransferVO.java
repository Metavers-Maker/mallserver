package com.muling.mall.wms.pojo.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("视图对象")
public class TransferVO {

    private Long id;

    private Integer coinType;

    private Long minValue;

    private Long maxValue;

    private Integer feeType;

    private Double fee;

    /**
     * 0-可用 1-不可用
     */
    private Integer status;

    private String remark;
}
