package com.muling.mall.chat.test;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;

/**
 * 自定义类型扫描器，扫描BizUpdateHandler业务组件
 */
public class BizUpdateHandlerDefinitionScanner extends ClassPathBeanDefinitionScanner {
    public BizUpdateHandlerDefinitionScanner(BeanDefinitionRegistry registry, ResourceLoader resourceLoader,
                                             Environment environment) {
        super(registry, false);
        addIncludeFilter(new AnnotationTypeFilter(BizUpdateHandler.class));
        setResourceLoader(resourceLoader);
        setEnvironment(environment);
    }
}
