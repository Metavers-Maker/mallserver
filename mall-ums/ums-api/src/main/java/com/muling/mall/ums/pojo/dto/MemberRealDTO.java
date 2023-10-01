package com.muling.mall.ums.pojo.dto;

import com.muling.mall.ums.enums.FollowStatusEnum;
import lombok.Data;

import java.util.Date;

@Data
public class MemberRealDTO {
    /**
     * 真实姓名
     * */
    private String realName;

    /**
     * 身份证号
     * */
    private String idCard;
}
