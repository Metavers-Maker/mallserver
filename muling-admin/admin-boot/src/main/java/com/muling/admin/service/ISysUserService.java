package com.muling.admin.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muling.admin.dto.UserAuthDTO;
import com.muling.admin.pojo.entity.SysUser;
import com.muling.admin.pojo.form.UserForm;
import com.muling.admin.pojo.query.UserPageQuery;
import com.muling.admin.pojo.vo.user.UserDetailVO;
import com.muling.admin.pojo.vo.user.UserPageVO;

public interface ISysUserService extends IService<SysUser> {

    /**
     * 用户分页列表
     *
     * @return
     */
    IPage<UserPageVO> listUsersPage(UserPageQuery queryParams);

    /**
     * 新增用户
     *
     * @param userForm
     * @return
     */
    boolean saveUser(UserForm userForm);

    /**
     * 修改用户
     *
     * @param userForm
     * @return
     */
    boolean updateUser(Long userId, UserForm userForm);

    /**
     * 根据用户名获取认证用户信息，携带角色和密码
     *
     * @param username
     * @return
     */
    UserAuthDTO getAuthInfoByUsername(String username);

    /**
     * 根据用户ID获取用户详情
     *
     * @param userId
     * @return
     */
    public UserDetailVO getUserDetail(Long userId);

}
