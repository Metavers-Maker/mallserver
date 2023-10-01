package com.muling.mall.ums.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@ApiModel("会员导出对象")
@Data
@Accessors(chain = true)
public class MemberExportVO {

    @ApiModelProperty("会员ID")
    private Long id;

    @ApiModelProperty("会员昵称")
    private String nickName;

    @ApiModelProperty("会员邮箱")
    private String email;

    @ApiModelProperty("会员展示ID")
    private String uid;

    @ApiModelProperty("会员手机号")
    private String mobile;

    /**
     * 状态(1:正常；0：禁用)
     */
    @ApiModelProperty("用户状态")
    private String status;

    @ApiModelProperty("实名状态")
    private String authStatus;

    @ApiModelProperty("创建时间")
    private LocalDateTime created;

}
