package com.muling.mall.chat.test;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

public class VideoMsgUpdateComponentRegistry implements ImportBeanDefinitionRegistrar, EnvironmentAware, ResourceLoaderAware {

    private Environment environment;
    private ResourceLoader resourceLoader;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        EnableScanBizUpdateHandler synthesize = importingClassMetadata.getAnnotations().get(EnableScanBizUpdateHandler.class).synthesize();
        BizUpdateHandlerDefinitionScanner bizUpdateHandlerDefinitonScanner = new BizUpdateHandlerDefinitionScanner(registry,
                this.resourceLoader, this.environment);
        bizUpdateHandlerDefinitonScanner.scan(synthesize.pkg());
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
