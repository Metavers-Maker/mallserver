package com.muling.mall.bms.pojo.vo.app;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
public class OpenItemVO {

    private Long spuId;

    private Integer type;

    private String name;

    private String picUrl;

    private Integer count;

    private Integer coinType;

    private BigDecimal balance;

    private List<String> itemNos;

}
