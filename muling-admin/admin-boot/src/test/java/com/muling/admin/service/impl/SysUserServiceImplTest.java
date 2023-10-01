package com.muling.admin.service.impl;

import com.muling.admin.pojo.form.UserForm;
import com.muling.admin.service.ISysUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * @author <a href="mailto:xianrui0365@163.com">xianrui</a>
 * @date 2021/8/28
 */
@SpringBootTest
class SysUserServiceImplTest {

    @Autowired
    private ISysUserService iSysUserService;

    @Test
    public void saveUser() {
        UserForm user=new UserForm();
        user.setUsername("root");
        user.setDeptId(1L);
        user.setNickname("沐灵技术");
        user.setMobile("17621590365");
        user.setEmail("mulingtech@163.com");
        iSysUserService.saveUser(user);
    }
}
