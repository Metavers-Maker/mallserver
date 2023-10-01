package com.muling.mall.pms.pojo.query;

import com.muling.common.base.BasePageQuery;
import lombok.Data;

/**
 *
 */

@Data
public class SubjectConfigPageQuery extends BasePageQuery {

    private Long spu_id;

    private Long subject_id;
}
