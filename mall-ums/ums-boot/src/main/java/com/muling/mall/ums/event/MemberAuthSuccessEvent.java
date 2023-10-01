package com.muling.mall.ums.event;

import com.muling.mall.ums.pojo.entity.UmsMember;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MemberAuthSuccessEvent {

    private UmsMember member;
}
