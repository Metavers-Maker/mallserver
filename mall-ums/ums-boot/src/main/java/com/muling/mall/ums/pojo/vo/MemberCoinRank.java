package com.muling.mall.ums.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@ApiModel("用户积分排行视图层对象")
@Data
public class MemberCoinRank {

    @ApiModelProperty("会员ID")
    private Long id;

    @ApiModelProperty("会员昵称")
    private String nickName;

    @ApiModelProperty("会员展示ID")
    private String uid;

    @ApiModelProperty("会员头像地址")
    private String avatarUrl;

    @ApiModelProperty("积分数量")
    private BigDecimal balance;

    @ApiModelProperty("创建时间")
    private Long created;

}
