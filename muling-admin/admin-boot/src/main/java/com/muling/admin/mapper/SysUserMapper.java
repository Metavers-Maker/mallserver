package com.muling.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.admin.dto.UserAuthDTO;
import com.muling.admin.pojo.entity.SysUser;
import com.muling.admin.pojo.query.UserPageQuery;
import com.muling.admin.pojo.vo.user.UserDetailVO;
import com.muling.admin.pojo.vo.user.UserPageVO;
import com.muling.common.mybatis.annotation.DataPermission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户持久层
 *
 * @author haoxr
 * @date 2022/1/14
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 获取用户分页列表
     *
     * @param pageParam
     * @param queryParams
     * @return
     */
//    @DataPermission(deptAlias = "d")
    List<UserPageVO> listUsersPage(Page<UserPageVO> pageParam, UserPageQuery queryParams);

    /**
     * 根据用户ID获取用户详情
     *
     * @param userId
     * @return
     */
    UserDetailVO getUserDetail(Long userId);

    /**
     * 根据用户名获取认证信息
     *
     * @param username
     * @return
     */
    UserAuthDTO getAuthInfoByUsername(String username);


}
