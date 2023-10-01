package com.muling.mall.chat.test;


import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(VideoMsgUpdateComponentRegistry.class)
public @interface EnableScanBizUpdateHandler {
    /**
     * 要扫描的包
     */
    String pkg();
}
