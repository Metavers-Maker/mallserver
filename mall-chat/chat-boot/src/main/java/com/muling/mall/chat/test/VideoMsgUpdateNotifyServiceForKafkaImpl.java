package com.muling.mall.chat.test;


/**
 * 发送kafka通知其他业务方
 * @since 2022-04-14 22:31
 */
@BizUpdateHandler
public class VideoMsgUpdateNotifyServiceForKafkaImpl implements VideoMsgUpdateNotifyService {
    @Override
    public void updateNotify(VideoModel videoModel) {
        System.out.println("模拟发送kafka通知");
    }
}
