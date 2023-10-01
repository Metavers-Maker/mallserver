package com.muling.admin.service.impl;

import com.muling.admin.mapper.SysPermissionMapper;
import com.muling.admin.pojo.entity.SysPermission;
import com.muling.admin.pojo.form.PathRuleForm;
import com.muling.admin.service.ISysPathRuleService;
import com.muling.admin.service.ISysPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author haoxr
 * @description TODO
 * @createTime 2021/5/30 23:19
 */
@SpringBootTest
@Slf4j
class SysPermissionServiceImplTest {

    @Autowired
    SysPermissionMapper sysPermissionMapper;
    @Autowired
    ISysPermissionService iSysPermissionService;
    @Autowired
    private ISysPathRuleService sysPathRuleService;

    @Test
    void listPermissionRoles() {
        List<SysPermission> sysPermissions = sysPermissionMapper.listPermRoles();
        log.info(sysPermissions.toString());
    }

    @Test
    void refreshPermRolesRules() {
        PathRuleForm pathRuleForm = new PathRuleForm();
        pathRuleForm.setRemark("fda");
        boolean update = sysPathRuleService.update(1L, pathRuleForm);
        log.info(update + "");
    }
}
