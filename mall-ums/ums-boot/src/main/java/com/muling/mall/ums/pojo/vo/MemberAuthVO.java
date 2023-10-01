package com.muling.mall.ums.pojo.vo;


import com.baomidou.mybatisplus.annotation.TableName;
import com.muling.mall.ums.enums.AuthStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@TableName(autoResultMap = true)
@Accessors(chain = true)
public class MemberAuthVO {

    private String realName;

    private String idCard;

    private AuthStatusEnum status;

}
