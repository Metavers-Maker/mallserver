package com.muling.admin.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muling.admin.constant.SystemConstants;
import com.muling.admin.converter.UserConverter;
import com.muling.admin.dto.UserAuthDTO;
import com.muling.admin.mapper.SysUserMapper;
import com.muling.admin.pojo.entity.SysUser;
import com.muling.admin.pojo.form.UserForm;
import com.muling.admin.pojo.query.UserPageQuery;
import com.muling.admin.pojo.vo.user.UserDetailVO;
import com.muling.admin.pojo.vo.user.UserPageVO;
import com.muling.admin.service.ISysUserRoleService;
import com.muling.admin.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户业务类
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    private final PasswordEncoder passwordEncoder;
    private final ISysUserRoleService sysUserRoleService;

    /**
     * 获取用户分页列表
     *
     * @param queryParams
     * @return
     */
    @Override
    public IPage<UserPageVO> listUsersPage(UserPageQuery queryParams) {
        Page<UserPageVO> page = new Page<>(queryParams.getPageNum(), queryParams.getPageSize());
        List<UserPageVO> list = this.baseMapper.listUsersPage(page, queryParams);
        page.setRecords(list);
        return page;
    }

    /**
     * 新增用户
     *
     * @param userForm
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveUser(UserForm userForm) {

        String username = userForm.getUsername();

        long count = this.count(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        Assert.isTrue(count == 0, "用户名已存在");

        // 实体转换 form->entity
        SysUser entity = UserConverter.INSTANCE.form2Entity(userForm);

        // 设置默认加密密码
        String defaultEncryptPwd = passwordEncoder.encode(SystemConstants.DEFAULT_USER_PASSWORD);
        entity.setPassword(defaultEncryptPwd);

        // 新增用户
        boolean result = this.save(entity);

        if (result) {
            // 保存用户角色
            sysUserRoleService.saveUserRoles(entity.getId(), userForm.getRoleIds());
        }
        return result;
    }

    /**
     * 更新用户
     *
     * @param userId   用户ID
     * @param userForm 用户表单对象
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(Long userId, UserForm userForm) {
        String username = userForm.getUsername();

        long count = this.count(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .ne(SysUser::getId, userId)
        );
        Assert.isTrue(count == 0, "用户名已存在");

        // form -> entity
        SysUser entity = UserConverter.INSTANCE.form2Entity(userForm);

        // 修改用户
        boolean result = this.updateById(entity);

        if (result) {
            // 保存用户角色
            sysUserRoleService.saveUserRoles(entity.getId(), userForm.getRoleIds());
        }
        return result;
    }

    /**
     * 根据用户名获取认证信息
     *
     * @param username
     * @return
     */
    @Override
    public UserAuthDTO getAuthInfoByUsername(String username) {
        UserAuthDTO userAuthInfo = this.baseMapper.getAuthInfoByUsername(username);
        return userAuthInfo;
    }

    /**
     * 根据用户ID获取用户详情
     *
     * @param userId
     * @return
     */
    @Override
    public UserDetailVO getUserDetail(Long userId) {
        UserDetailVO userDetail = this.baseMapper.getUserDetail(userId);
        return userDetail;
    }

}
