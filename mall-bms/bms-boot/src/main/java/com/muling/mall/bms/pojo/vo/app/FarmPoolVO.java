package com.muling.mall.bms.pojo.vo.app;

import lombok.Data;

@Data
public class FarmPoolVO {

    private Long id;

    private String name;

    private String data;

    private Long spuId;

    private Long balance;

    private Double totalAllocPoint;

    private String remark;

    private Integer status;

    private Long allocated;

}
