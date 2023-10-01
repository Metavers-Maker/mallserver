package com.muling.mall.task.pojo.vo.app;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class TaskItemRewardVO {

    private Integer coinType;

    private BigDecimal coinValue;

    private Long itemId;

    private Integer itemCount;
}
