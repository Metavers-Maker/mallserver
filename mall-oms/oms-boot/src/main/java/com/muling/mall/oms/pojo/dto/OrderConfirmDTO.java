package com.muling.mall.oms.pojo.dto;

import lombok.Data;

/**
 * @author huawei
 * @desc 订单提交实体类
 * @email huawei_code@163.com
 * @date 2021/1/16
 */
@Data
public class OrderConfirmDTO {

    private Long skuId;

    private Integer count;

}
