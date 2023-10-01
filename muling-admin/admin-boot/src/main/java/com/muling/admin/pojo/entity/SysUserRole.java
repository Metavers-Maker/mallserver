package com.muling.admin.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Accessors(chain = true)
public class SysUserRole {

    private Long userId;

    private Long roleId;

}
