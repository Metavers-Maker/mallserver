package com.muling.mall.bms.pojo.vo.app;

import lombok.Data;

@Data
public class TransferVO {

    private Long id;

    /**
     * 消耗虚拟币种 0-积分
     */
    private Integer type;

    private Long spuId;

    private Long typeValue;

    private Long icd;

    private Long ocd;
    /**
     * 0-可用 1-不可用
     */
    private Integer status;

    private String remark;

}
