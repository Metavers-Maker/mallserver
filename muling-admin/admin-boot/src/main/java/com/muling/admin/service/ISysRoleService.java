package com.muling.admin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.admin.pojo.entity.SysRole;

import java.util.List;

public interface ISysRoleService extends IService<SysRole> {

    boolean delete(List<Long> ids);
}
