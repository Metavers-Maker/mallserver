package com.muling.common.protocol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author chen
 */
@Data
@ApiModel
public class StatisticsRequest {
    @NotNull
    /***
     * 1 按天
     * 2 按月
     */
    @NotNull
    @ApiModelProperty(value = "按天/按月查询，1表示按天，2表示按月", required = true)
    private Integer timeDimension;

    @NotNull
    @ApiModelProperty(value = "查询开始时间", required = true)
    private Long beginTime;

    @NotNull
    @ApiModelProperty(value = "查询结束时间", required = true)
    private Long endTime;

    /**
     * 时区偏移量，秒
     */
    @ApiModelProperty(value = "时区偏移量，预留")
    private Integer offset = 28800;
}
