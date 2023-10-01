package com.muling.common.mybatis.annotation;

import java.lang.annotation.*;

/**
 * 数据权限注解
 * 参考链接: https://gitee.com/baomidou/mybatis-plus/issues/I37I90
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface DataPermission {

    /**
     * 数据权限 {@link com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor}
     */
    String deptAlias() default "";
}

