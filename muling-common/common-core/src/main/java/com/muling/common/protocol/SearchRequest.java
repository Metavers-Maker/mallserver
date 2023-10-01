package com.muling.common.protocol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author chen
 */
@Data
@ApiModel
public class SearchRequest {

    @ApiModelProperty("搜索关键字")
    private String key;

}
