package com.muling.admin.controller;

import com.muling.admin.pojo.entity.SysMenu;
import com.muling.admin.pojo.vo.menu.MenuVO;
import com.muling.admin.pojo.vo.menu.RouteVO;
import com.muling.admin.service.ISysMenuService;
import com.muling.admin.service.ISysPermissionService;
import com.muling.common.annotation.AutoLog;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.result.Result;
import com.muling.common.web.domain.OptionVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 菜单控制器
 *
 * @author <a href="mailto:xianrui0365@163.com">xianrui</a>
 * @date 2020-11-06
 */
@Api(tags = "admin-菜单")
@RestController
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
@Slf4j
public class MenuController {

    private final ISysMenuService menuService;
    private final ISysPermissionService permissionService;

    @ApiOperation(value = "菜单列表")
    @GetMapping("/table")
    public Result getTableList(
            @ApiParam("菜单名称") String name) {
        List<MenuVO> menuList = menuService.listTable(name);
        return Result.success(menuList);
    }

    @ApiOperation(value = "菜单下拉列表")
    @GetMapping("/select")
    public Result listMenuOptions() {
        List<OptionVO> menuList = menuService.listMenuOptions();
        return Result.success(menuList);
    }

    @ApiOperation(value = "路由列表")
    @GetMapping("/route")
    public Result listRoutes() {
        List<RouteVO> routeList = menuService.listRoutes();
        return Result.success(routeList);
    }


    @ApiOperation(value = "菜单详情")
    @GetMapping("/{id}")
    public Result detail(
            @ApiParam(value = "菜单ID") @PathVariable Long id) {
        SysMenu menu = menuService.getById(id);
        return Result.success(menu);
    }

    @ApiOperation(value = "创建菜单")
    @PostMapping
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    public Result add(@RequestBody SysMenu menu) {
        boolean result = menuService.saveMenu(menu);
        return Result.judge(result);
    }

    @ApiOperation(value = "修改菜单")
    @PutMapping(value = "/{id}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result update(@PathVariable Long id, @RequestBody SysMenu menu) {
        boolean result = menuService.updateMenu(menu);
        return Result.judge(result);
    }

    @ApiOperation(value = "删除菜单")
    @DeleteMapping("/{ids}")
    @CacheEvict(cacheNames = "system", key = "'routes'")
    @AutoLog(operateType = LogOperateTypeEnum.DELETE, logType = LogTypeEnum.OPERATE)
    public Result delete(@PathVariable("ids") String ids) {
        boolean result = menuService.removeByIds(Arrays.asList(ids.split(",")));
        if (result) {
            permissionService.refreshPermRolesRules();
        }
        return Result.judge(result);
    }

    @ApiOperation(value = "修改菜单显示状态")
    @PatchMapping("/{id}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result updateMenuVisible(
            @ApiParam(value = "菜单ID") @PathVariable Long menuId,
            @ApiParam(value = "显示状态(1-显示；2-隐藏)") Integer visible

    ) {
        boolean result = menuService.updateMenuVisible(menuId, visible);
        return Result.judge(result);
    }
}
