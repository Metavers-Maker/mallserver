package com.muling.mall.ums.pojo.vo;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("会员视图层对象")
@Data
public class MemberVO {

    @ApiModelProperty("会员ID")
    private Long id;

    @ApiModelProperty("会员昵称")
    private String nickName;

    @ApiModelProperty("会员邮箱")
    private String email;

    @ApiModelProperty("会员展示ID")
    private String uid;

    @ApiModelProperty("会员头像地址")
    private String avatarUrl;

    @ApiModelProperty("会员手机号")
    private String mobile;

    @ApiModelProperty("Alipay")
    private String alipay;

    @ApiModelProperty("Wechat")
    private String wechat;

    @ApiModelProperty("链地址")
    private String chainAddress;
    /**
     * 状态(1:正常；0：禁用)
     */
    @ApiModelProperty("用户状态")
    private Integer status;

    @ApiModelProperty("实名状态")
    private Integer authStatus;

    @ApiModelProperty("扩展信息")
    private Object ext;

    @ApiModelProperty("安全码")
    private String safeCode;

    @ApiModelProperty("是否注销")
    private Integer deleted;

    @ApiModelProperty("是否绑定wx")
    private Boolean hasWx;

    @ApiModelProperty("是否绑定支付宝")
    private Boolean hasAlipay;

    @ApiModelProperty("是否设置交易密码")
    private Boolean hasTradeCode;

    @ApiModelProperty("创建时间")
    private Long created;

}
