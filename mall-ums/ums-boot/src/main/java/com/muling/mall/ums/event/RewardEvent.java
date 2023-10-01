package com.muling.mall.ums.event;

import com.muling.mall.ums.pojo.entity.UmsMember;
import lombok.Data;

@Data
public class RewardEvent {

    private UmsMember member;

    private Integer type;
}
