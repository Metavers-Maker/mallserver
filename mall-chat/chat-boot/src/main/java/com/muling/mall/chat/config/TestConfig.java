package com.muling.mall.chat.config;

import com.muling.mall.chat.test.EnableScanBizUpdateHandler;
import com.muling.mall.chat.test.VideoModel;
import com.muling.mall.chat.test.VideoMsgUpdateNotifyService;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 使用此注解来标识使能WebSocket的broker.即使用broker来处理消息.
 */
@Configuration
@EnableScanBizUpdateHandler(pkg = "com.muling.mall.chat.test.**")
public class TestConfig {

    @Resource
    private VideoMsgUpdateNotifyService videoMsgUpdateNotifyService;


    @PostConstruct
    public void init() {
        videoMsgUpdateNotifyService.updateNotify(new VideoModel());
    }
}
