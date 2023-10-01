package com.muling.mall.ums.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class MemberListByIds {

    private List<Long> memberIds;

}
