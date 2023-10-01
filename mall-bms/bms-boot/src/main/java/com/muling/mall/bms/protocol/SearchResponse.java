package com.muling.mall.bms.protocol;

import com.muling.mall.bms.pojo.vo.app.MarketVO;
import com.muling.mall.bms.pojo.vo.app.MemberItemVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel
public class SearchResponse {

    @ApiModelProperty("二级市场列表")
    List<MarketVO> markets;
}
