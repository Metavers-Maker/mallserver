package com.muling.mall.ums.event;

import com.muling.mall.ums.pojo.dto.MemberRegisterDTO;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MemberRegisterEvent {

    private MemberRegisterDTO inviteMember;

    private MemberRegisterDTO member;
}
