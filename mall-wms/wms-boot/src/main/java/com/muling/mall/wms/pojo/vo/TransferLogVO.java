package com.muling.mall.wms.pojo.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("视图对象")
public class TransferLogVO {

    private Long sourceId;

    private Long targetId;

    private String targetUid;

    private Integer coinType;

    private Long balance;

    private Double fee;

    private String remark;

    private Long created;
}
