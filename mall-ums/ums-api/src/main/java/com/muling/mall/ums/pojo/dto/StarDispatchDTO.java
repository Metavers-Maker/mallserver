package com.muling.mall.ums.pojo.dto;

import com.muling.mall.ums.enums.FollowStatusEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class StarDispatchDTO {
    private Integer star;
    private BigDecimal fee;
}
