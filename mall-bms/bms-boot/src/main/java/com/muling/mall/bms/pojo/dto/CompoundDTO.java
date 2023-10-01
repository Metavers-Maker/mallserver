package com.muling.mall.bms.pojo.dto;

import lombok.Data;

/**
 * 订单商品明细
 */
@Data
public class CompoundDTO {

    private Long[] itemIds;

    private Long compoundId;
}
