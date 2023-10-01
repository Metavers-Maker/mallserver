package com.muling.mall.ums.pojo.vo;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@TableName(autoResultMap = true)
@Accessors(chain = true)
public class MemberInviteVO {

    private Long memberId;

    private String inviteCode;

    private Integer authStatus;

    private String ext;

    private Long created;

}
