package com.muling.mall.chat.test;

/**
 * 模拟更新ES数据
 * @since 2022-04-14 22:29
 */
@BizUpdateHandler
public class VideoMsgUpdateNotifyServiceForESImpl implements VideoMsgUpdateNotifyService {
    @Override
    public void updateNotify(VideoModel videoModel) {
        System.out.println("模拟更新ES数据");
    }
}
