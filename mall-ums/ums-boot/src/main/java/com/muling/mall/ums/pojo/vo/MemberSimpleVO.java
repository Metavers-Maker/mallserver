package com.muling.mall.ums.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("会员视图层对象")
@Data
public class MemberSimpleVO {

    @ApiModelProperty("会员昵称")
    private String nickName;

    @ApiModelProperty("会员展示ID")
    private String uid;

    @ApiModelProperty("安全码")
    private String safeCode;

    @ApiModelProperty("链地址")
    private String chainAddress;
}
