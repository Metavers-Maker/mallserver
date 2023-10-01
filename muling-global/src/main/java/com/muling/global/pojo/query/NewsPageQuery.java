package com.muling.global.pojo.query;

import com.muling.common.base.BasePageQuery;
import lombok.Data;


@Data
public class NewsPageQuery extends BasePageQuery {

    private String title;

    private Integer type;

}
