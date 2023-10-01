package com.muling.mall.ums.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("视图层对象")
@Data
public class InviteLogVO {

//    private Long id;

//    @ApiModelProperty("会员Id")
//    private Long memberId;

//    @ApiModelProperty("会员昵称")
//    private String memberName;

//    @ApiModelProperty("被邀请者Id")
//    private Long inviteeId;

    @ApiModelProperty("被邀请者昵称")
    private String inviteeName;

    @ApiModelProperty("被邀请者手机号")
    private String inviteeMobile;

    @ApiModelProperty("认证状态")
    private Integer status;

    @ApiModelProperty("创建时间")
    private Long created;

}
