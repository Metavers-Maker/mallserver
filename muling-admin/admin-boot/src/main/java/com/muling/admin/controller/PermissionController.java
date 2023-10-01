package com.muling.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.muling.admin.pojo.entity.SysPermission;
import com.muling.admin.pojo.query.PermissionPageQuery;
import com.muling.admin.pojo.vo.permission.PermissionPageVO;
import com.muling.admin.service.ISysPermissionService;
import com.muling.common.annotation.AutoLog;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Api(tags = "admin-权限")
@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final ISysPermissionService permissionService;

    @ApiOperation(value = "权限分页列表")
    @GetMapping("/page")
    public PageResult<PermissionPageVO> listPermPages(
            PermissionPageQuery permissionPageQuery
    ) {
        IPage<PermissionPageVO> result = permissionService.listPermPages(permissionPageQuery);
        return PageResult.success(result);
    }

    @ApiOperation(value = "权限列表")
    @GetMapping
    public Result list(Long menuId) {
        List<SysPermission> list = permissionService.list(new LambdaQueryWrapper<SysPermission>()
                .eq(SysPermission::getMenuId, menuId));
        return Result.success(list);
    }

    @ApiOperation(value = "权限详情")
    @GetMapping("/{id}")
    public Result detail(
            @ApiParam("权限ID") @PathVariable Long id) {
        SysPermission permission = permissionService.getById(id);
        return Result.success(permission);
    }

    @ApiOperation(value = "新增权限")
    @PostMapping
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    public Result add(@RequestBody SysPermission permission) {
        boolean result = permissionService.save(permission);
        if (result) {
            permissionService.refreshPermRolesRules();
        }
        return Result.judge(result);
    }

    @ApiOperation(value = "修改权限")
    @PutMapping(value = "/{id}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result update(
            @PathVariable Long id,
            @RequestBody SysPermission permission) {
        boolean result = permissionService.updateById(permission);
        if (result) {
            permissionService.refreshPermRolesRules();
        }
        return Result.judge(result);
    }

    @ApiOperation(value = "删除权限")
    @DeleteMapping("/{ids}")
    @AutoLog(operateType = LogOperateTypeEnum.DELETE, logType = LogTypeEnum.OPERATE)
    public Result delete(@PathVariable String ids) {
        boolean status = permissionService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.judge(status);
    }
}
