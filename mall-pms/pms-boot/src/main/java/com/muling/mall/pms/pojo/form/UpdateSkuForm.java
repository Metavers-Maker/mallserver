package com.muling.mall.pms.pojo.form;

import lombok.Data;

@Data
public class UpdateSkuForm {

    /**
     * SKU 名称
     */
    private String name;

    /**
     * 商品价格(单位：分)
     */
    private Long price;

    /**
     * 关闭销售
     */
    private Integer closed;

    /**
     * 商品主图
     */
    private String picUrl;
}
