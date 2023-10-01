package com.muling.mall.pms.pojo.query;

import com.muling.common.base.BasePageQuery;
import lombok.Data;

/**
 * 商品分页查询对象
 *
 * @author <a href="mailto:xianrui0365@163.com">haoxr</a>
 * @date 2022/2/5 13:09
 */

@Data
public class SkuAdminPageQuery extends BasePageQuery {

    private Long spuId;

}
