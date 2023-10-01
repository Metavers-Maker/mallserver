package com.muling.admin.component.cache;

import com.muling.admin.service.ISysPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 容器启动完成时加载角色权限规则至Redis缓存
 */
@Component
@RequiredArgsConstructor
public class InitPermissionRolesCache implements CommandLineRunner {

    private final ISysPermissionService sysPermissionService;

    @Override
    public void run(String... args) {
        sysPermissionService.refreshPermRolesRules();
    }
}
