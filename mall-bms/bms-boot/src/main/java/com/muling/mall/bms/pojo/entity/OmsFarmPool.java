package com.muling.mall.bms.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.muling.common.base.BaseEntity;
import com.muling.mall.bms.enums.StatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 锁仓配置表
 */
@Data
@Accessors(chain = true)
public class OmsFarmPool extends BaseEntity {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String data;

    private Long spuId;

    private BigDecimal balance;

    private Double totalAllocPoint;

    private String remark;

    private StatusEnum status;

    private Integer days;

    private BigDecimal dayAmount;

    private Integer currentDays;

//    @JsonInclude(value = JsonInclude.Include.NON_NULL)
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private LocalDateTime allocated;


    @Data
    public static class Rule {
        private Integer days;
        private Double allocPoint;
    }
}
