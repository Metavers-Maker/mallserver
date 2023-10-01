package com.muling.mall.pms.pojo.form;

import lombok.Data;

@Data
public class GroundForm {

    /**
     * 广场名称
     * */
    private String name;

    /**
     * 广场类型
     * */
    private Integer type;

    /**
     * 商品ID
     * */
    private Long spuId;

    /**
     * 排序
     * */
    private Integer sort;

}
