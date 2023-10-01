package com.muling.admin.pojo.query;

import com.muling.common.base.BasePageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 分页查询对象
 */
@Data
@ApiModel
public class OauthClientPageQuery extends BasePageQuery {

    @ApiModelProperty("客户端ID")
    String clientId;

}
