package com.muling.mall.pms.protocol;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class SpuListByIdsRequest {

    private List<Long> spuIds;

}
