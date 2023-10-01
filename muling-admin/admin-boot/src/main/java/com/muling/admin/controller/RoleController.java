package com.muling.admin.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.admin.pojo.entity.SysRole;
import com.muling.admin.pojo.form.RolePermsForm;
import com.muling.admin.service.ISysPermissionService;
import com.muling.admin.service.ISysRoleMenuService;
import com.muling.admin.service.ISysRolePermissionService;
import com.muling.admin.service.ISysRoleService;
import com.muling.common.annotation.AutoLog;
import com.muling.common.constant.GlobalConstants;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.result.Result;
import com.muling.common.web.util.UserUtils;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "admin-角色")
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final ISysRoleService roleService;
    private final ISysRoleMenuService roleMenuService;
    private final ISysRolePermissionService rolePermissionService;
    private final ISysPermissionService permissionService;


    @ApiOperation(value = "角色分页列表")
    @GetMapping("/page")
    public Result pageList(Integer page, Integer limit, String name) {
        List<String> roles = UserUtils.getRoles();
        boolean isRoot = roles.contains(GlobalConstants.ROOT_ROLE_CODE);  // 判断是否是超级管理员
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<SysRole>()
                .like(StrUtil.isNotBlank(name), SysRole::getName, name)
                .ne(!isRoot, SysRole::getCode, GlobalConstants.ROOT_ROLE_CODE)
                .orderByAsc(SysRole::getSort)
                .orderByDesc(SysRole::getUpdated)
                .orderByDesc(SysRole::getCreated);
        Page<SysRole> result = roleService.page(new Page<>(page, limit), queryWrapper);
        return Result.success(result.getRecords(), result.getTotal());
    }


    @ApiOperation(value = "角色列表")
    @GetMapping
    public Result list() {
        List<String> roles = UserUtils.getRoles();
        boolean isRoot = roles.contains(GlobalConstants.ROOT_ROLE_CODE);  // 判断是否是超级管理员
        List list = roleService.list(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getStatus, GlobalConstants.STATUS_YES)
                .ne(!isRoot, SysRole::getCode, GlobalConstants.ROOT_ROLE_CODE)
                .orderByAsc(SysRole::getSort)
        );
        return Result.success(list);
    }


    @ApiOperation(value = "新增角色")
    @PostMapping
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    public Result add(@RequestBody SysRole role) {
        long count = roleService.count(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getCode, role.getCode())
                .or()
                .eq(SysRole::getName, role.getName())
        );
        Assert.isTrue(count == 0, "角色名称或角色编码重复，请检查！");
        boolean result = roleService.save(role);
        if (result) {
            permissionService.refreshPermRolesRules();
        }
        return Result.judge(result);
    }

    @ApiOperation(value = "修改角色")
    @PutMapping(value = "/{id}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result update(
            @PathVariable Long id,
            @RequestBody SysRole role) {
        long count = roleService.count(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getCode, role.getCode())
                .or()
                .eq(SysRole::getName, role.getName())
                .ne(SysRole::getId, id)
        );
        Assert.isTrue(count == 0, "角色名称或角色编码重复，请检查！");
        boolean result = roleService.updateById(role);
        if (result) {
            permissionService.refreshPermRolesRules();
        }
        return Result.judge(result);
    }

    @ApiOperation(value = "删除角色")
    @DeleteMapping("/{ids}")
    @AutoLog(operateType = LogOperateTypeEnum.DELETE, logType = LogTypeEnum.OPERATE)
    public Result delete(@PathVariable String ids) {
        boolean result = roleService.delete(Arrays.asList(ids.split(",")).stream()
                .map(id -> Long.parseLong(id)).collect(Collectors.toList()));
        if (result) {
            permissionService.refreshPermRolesRules();
        }
        return Result.judge(result);
    }

    @ApiOperation(value = "修改角色状态")
    @PatchMapping(value = "/{roleId}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result updateRolePart(@PathVariable Long roleId, @RequestBody SysRole role) {
        LambdaUpdateWrapper<SysRole> updateWrapper = new LambdaUpdateWrapper<SysRole>()
                .eq(SysRole::getId, roleId)
                .set(role.getStatus() != null, SysRole::getStatus, role.getStatus());
        boolean result = roleService.update(updateWrapper);
        if (result) {
            permissionService.refreshPermRolesRules();
        }
        return Result.judge(result);
    }

    @ApiOperation(value = "获取角色的资源ID集合")
    @GetMapping("/{roleId}/menus")
    public Result listRoleMenu(@PathVariable Long roleId) {
        List<Long> menuIds = roleMenuService.listMenuIds(roleId);
        return Result.success(menuIds);
    }

    @ApiOperation(value = "获得角色的资源权限")
    @GetMapping("/{roleId}/permissions")
    public Result listRolePermission(@PathVariable Long roleId, Long menuId) {
        List<Long> permissionIds = rolePermissionService.listPermIds(menuId, roleId);
        return Result.success(permissionIds);
    }

    @ApiOperation(value = "更新角色")
    @PutMapping(value = "/{roleId}/menus")
    @CacheEvict(cacheNames = "system", key = "'routes'")
    public Result updateRoleMenu(
            @ApiParam("roleId") @PathVariable Long roleId,
            @RequestBody SysRole role) {

        List<Long> menuIds = role.getMenuIds();
        boolean result = roleMenuService.update(roleId, menuIds);
        if (result) {
            permissionService.refreshPermRolesRules();
        }
        return Result.judge(result);
    }

    @ApiOperation(value = "修改角色权限")
    @PutMapping(value = "/{roleId}/permissions")
    public Result saveRolePerms(
            @ApiParam("角色ID") @PathVariable Long roleId,
            @RequestBody RolePermsForm rolePerms) {
        rolePerms.setRoleId(roleId);
        boolean result = rolePermissionService.saveRolePerms(rolePerms);
        if (result) {
            permissionService.refreshPermRolesRules();
        }
        return Result.judge(result);
    }
}
