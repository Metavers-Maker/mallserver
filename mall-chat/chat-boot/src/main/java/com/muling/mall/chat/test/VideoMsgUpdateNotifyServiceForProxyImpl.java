package com.muling.mall.chat.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;

/**
 * 类级别使用代理模式
 *
 * @since 2022-04-14 22:38
 */
@Primary
@BizUpdateHandler
public class VideoMsgUpdateNotifyServiceForProxyImpl implements VideoMsgUpdateNotifyService {

    @Autowired
    private List<VideoMsgUpdateNotifyService> videoMsgUpdateNotifyServices = new ArrayList<>();

    @Override
    public void updateNotify(VideoModel videoModel) {
        /**
         * 使用策略模式，这里不是装逼哈，哈哈
         */
        for (VideoMsgUpdateNotifyService videoMsgUpdateNotifyService : videoMsgUpdateNotifyServices) {
            videoMsgUpdateNotifyService.updateNotify(videoModel);
        }
    }
}
