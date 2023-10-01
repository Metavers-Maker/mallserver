package com.muling.admin.protocol;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author chen
 */
@Data
public class BusinessDateRangeRequest {

    @NotNull
    private Long beginTime;

    @NotNull
    private Long endTime;

    /**
     * 时区偏移量，秒
     */
    @NotNull
    private Integer offset = 0;

    /**
     * 是否矫正时间
     */
    private boolean correctTime = true;

    private String dateType;

}
