package com.muling.mall.pms.pojo.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 系列配置视图对象
 *
 */
@Data
@ApiModel("系列配置详情")
public class SubjectConfigVO {

    private Long id;
    private Long spuId;
    private Long updated;
}
