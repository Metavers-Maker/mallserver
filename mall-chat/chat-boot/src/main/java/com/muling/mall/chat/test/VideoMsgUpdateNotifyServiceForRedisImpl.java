package com.muling.mall.chat.test;

/**
 * 更新redis
 * @since 2022-04-14 22:26
 */

@BizUpdateHandler
public class VideoMsgUpdateNotifyServiceForRedisImpl implements VideoMsgUpdateNotifyService {
    @Override
    public void updateNotify(VideoModel videoModel) {
        System.out.println("模拟通过redis操作更新缓存信息");
    }
}
