package com.muling.mall.pms.pojo.query;

import com.muling.common.base.BasePageQuery;
import lombok.Data;

@Data
public class SpuAdminPageQuery extends BasePageQuery {
    private Long subjectId;

    private Long brandId;

    private Long publishId;

    private String name;

    private Integer type;

}
