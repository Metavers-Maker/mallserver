package com.muling.mall.chat.test;

/**
 * 业务通知
 *
 * @since 2022-04-14 22:15
 */
public interface VideoMsgUpdateNotifyService {
    /**
     * 通知更新
     *
     * @param videoModel
     */
    void updateNotify(VideoModel videoModel);
}
