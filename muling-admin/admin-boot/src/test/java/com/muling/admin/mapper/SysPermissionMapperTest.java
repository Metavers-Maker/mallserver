package com.muling.admin.mapper;

import com.muling.admin.pojo.entity.SysPathRule;
import com.muling.admin.service.ISysPathRuleService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author haoxr
 * @description TODO
 * @createTime 2021/5/30 23:18
 */
@SpringBootTest
@Slf4j
class SysPermissionMapperTest {

    @Test
    void listPermRoles() {
    }

    @Autowired
    private ISysPathRuleService iSysPathRuleService;

    private SysPathRuleMapper sysPathRuleMapper;

    private
    @Test
    void list() throws Exception {

        SysPathRule byId = sysPathRuleMapper.selectById(1L);
        System.out.println(byId);
    }

}
