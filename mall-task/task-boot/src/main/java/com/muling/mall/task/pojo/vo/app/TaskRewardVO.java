package com.muling.mall.task.pojo.vo.app;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class TaskRewardVO {

    private Integer coinType;

    private BigDecimal coinValue;

    private Integer times;

    private Integer totalTimes;

}
