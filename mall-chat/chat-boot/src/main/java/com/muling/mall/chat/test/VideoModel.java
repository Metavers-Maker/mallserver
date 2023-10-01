package com.muling.mall.chat.test;

import lombok.Data;

@Data
public class VideoModel {

    /**
     * 视频id
     */
    private Integer id;
    /**
     * 封面图 16X9
     */
    private String pic16_9;
    /**
     * 封面图4X3
     */
    private String pic4_3;
    /**
     * 视频标题
     */
    private String title;
    /**
     * 视频播放地址
     */
    private String url;
    /**
     * 视频时长
     */
    private Integer duration;
    //其他等等了

}
