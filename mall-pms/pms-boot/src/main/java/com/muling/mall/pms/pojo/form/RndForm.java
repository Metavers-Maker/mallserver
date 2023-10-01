package com.muling.mall.pms.pojo.form;

import lombok.Data;

@Data
public class RndForm {

    private String name;

    private Long target;

    /**
     * 图片
     * */
    private String picUrl;

    /**
     * 最大数量
     * */
    private Integer maxCount;

    private Long spuId;

    private Integer spuCount;

    private Long skuId;

    private Integer coinType;

    private Integer coinCount;

    private Integer prod;

}
