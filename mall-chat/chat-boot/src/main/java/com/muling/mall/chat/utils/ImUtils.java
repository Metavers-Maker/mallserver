package com.muling.mall.chat.utils;

public class ImUtils {

    /**
     * 获取Topic的生成的路由键
     * 生成规则如下: websocket订阅的目的地 + "-user" + websocket的sessionId值。生成值类似:
     *
     * @param actualDestination
     * @param sessionId
     * @return
     */
    public static String getTopicRoutingKey(String actualDestination, String sessionId) {
        return actualDestination + "-user" + sessionId;
    }
}
