package com.muling.mall.ums.pojo.dto;

import com.muling.mall.ums.enums.FollowStatusEnum;
import lombok.Data;

import java.util.Date;

@Data
public class RelationDTO {
    private Long id;
    private Long memberId;
    private Long followId;
    private String followName;
    private String leagueName;
    private String avatarUrl;
    private FollowStatusEnum status;
    private Date updated;
}
