package com.muling.admin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.admin.pojo.entity.SysRolePermission;
import com.muling.admin.pojo.form.RolePermsForm;

import java.util.List;

public interface ISysRolePermissionService extends IService<SysRolePermission> {

    /**
     * 根据菜单ID和角色ID获取权限ID集合
     *
     * @param menuId
     * @param roleId
     * @return
     */
    List<Long> listPermIds(Long menuId, Long roleId);


    /**
     * 保存角色的权限
     *
     * @return
     */
    boolean saveRolePerms(RolePermsForm rolePermsForm);

}
