package com.muling.mall.pms.pojo.query;

import com.muling.common.base.BasePageQuery;
import lombok.Data;

/**
 * 系列查询
 */

@Data
public class SubjectPageQuery extends BasePageQuery {

    private String name;

    private Long[] brandIds;

    private Long subjectId;
}
