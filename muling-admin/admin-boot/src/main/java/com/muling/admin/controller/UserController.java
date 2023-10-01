package com.muling.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.muling.admin.dto.UserAuthDTO;
import com.muling.admin.pojo.entity.SysUser;
import com.muling.admin.pojo.form.UserForm;
import com.muling.admin.pojo.query.UserPageQuery;
import com.muling.admin.pojo.vo.user.LoginUserVO;
import com.muling.admin.pojo.vo.user.UserDetailVO;
import com.muling.admin.pojo.vo.user.UserPageVO;
import com.muling.admin.service.ISysPermissionService;
import com.muling.admin.service.ISysUserService;
import com.muling.common.annotation.AutoLog;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.common.web.util.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "admin-用户")
@RestController
@RequestMapping("/api/v1/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final ISysUserService userService;
    private final PasswordEncoder passwordEncoder;
    private final ISysPermissionService permissionService;

    @ApiOperation(value = "用户分页列表")
    @GetMapping("/page")
    public PageResult<UserPageVO> listUsersPage(
            UserPageQuery queryParams
    ) {
        IPage<UserPageVO> result = userService.listUsersPage(queryParams);
        return PageResult.success(result);
    }

    @ApiOperation(value = "获取用户表单详情")
    @GetMapping("/{userId}")
    public Result<UserDetailVO> getUserDetail(
            @ApiParam(value = "用户ID", example = "1") @PathVariable Long userId
    ) {
        UserDetailVO userDetail = userService.getUserDetail(userId);
        return Result.success(userDetail);
    }

    @ApiOperation(value = "新增用户")
    @PostMapping
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    public Result add(@RequestBody UserForm userForm) {
        boolean result = userService.saveUser(userForm);
        return Result.judge(result);
    }

    @ApiOperation(value = "修改用户")
    @PutMapping(value = "/{id}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result update(
            @ApiParam("用户ID") @PathVariable Long id,
            @RequestBody UserForm userForm) {
        boolean result = userService.updateUser(id, userForm);
        return Result.judge(result);
    }

    @ApiOperation(value = "删除用户")
    @DeleteMapping("/{ids}")
    @AutoLog(operateType = LogOperateTypeEnum.DELETE, logType = LogTypeEnum.OPERATE)
    public Result delete(
            @ApiParam("用户ID，多个以英文逗号(,)分割") @PathVariable String ids) {
        boolean status = userService.removeByIds(Arrays.asList(ids.split(",")).stream().collect(Collectors.toList()));
        return Result.judge(status);
    }

    @ApiOperation(value = "选择性修改用户")
    @PatchMapping(value = "/{id}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result patch(@PathVariable Long id, @RequestBody SysUser user) {
        LambdaUpdateWrapper<SysUser> updateWrapper = new LambdaUpdateWrapper<SysUser>().eq(SysUser::getId, id);
        updateWrapper.set(user.getStatus() != null, SysUser::getStatus, user.getStatus());
        updateWrapper.set(user.getPassword() != null, SysUser::getPassword, passwordEncoder.encode(user.getPassword()));
        boolean status = userService.update(updateWrapper);
        return Result.judge(status);
    }


    /**
     * 提供用于用户登录认证信息
     */
    @ApiOperation(value = "根据用户名获取认证信息")
    @GetMapping("/username/{username}")
    public Result<UserAuthDTO> getAuthInfoByUsername(
            @ApiParam("用户名") @PathVariable String username) {
        UserAuthDTO user = userService.getAuthInfoByUsername(username);
        return Result.success(user);
    }


    @ApiOperation(value = "获取当前登陆的用户信息")
    @GetMapping("/me")
    public Result<LoginUserVO> getCurrentUser() {
        LoginUserVO loginUserVO = new LoginUserVO();
        // 用户基本信息
        Long userId = UserUtils.getUserId();
        SysUser user = userService.getById(userId);
        BeanUtil.copyProperties(user, loginUserVO);
        // 用户角色信息
        List<String> roles = UserUtils.getRoles();
        loginUserVO.setRoles(roles);
        // 用户按钮权限信息
        List<String> perms = permissionService.listBtnPermByRoles(roles);
        loginUserVO.setPerms(perms);
        return Result.success(loginUserVO);
    }
}
