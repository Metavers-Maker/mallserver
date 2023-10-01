//
//  Created by  fred on 2017/1/12.
//  Copyright © 2016年 Alibaba. All rights reserved.
//

package com.muling.common.cert.service;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muling.common.cert.config.BSNConfig;
import com.muling.common.util.MD5Util;
import lombok.RequiredArgsConstructor;
import net.jodah.failsafe.RetryPolicy;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HttpApiClientWechat {

    private final CloseableHttpClient client;

    private Logger log = LoggerFactory.getLogger(getClass());


    public static final ObjectMapper mapper = new ObjectMapper();


    /**
     * Avatar-创建BSN账户
     */
    public JSONObject openLogin(String code) throws URISyntaxException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("api.weixin.qq.com")
                .setPath("/sns/oauth2/access_token")
                .setParameter("appid", "wx2704475ce0411827")
                .setParameter("secret", "3bf3ea2b634d34f3eb24be5bbad229ad")
                .setParameter("code", code)
                .setParameter("grant_type", "authorization_code")
                .build();
        return sendGet(uri);
    }

    public JSONObject sendGet(URI uri) {
        JSONObject ret = null;
        try {
            HttpGet httpGet = new HttpGet(uri);
            CloseableHttpResponse response = client.execute(httpGet);
            if (response != null) {
                HttpEntity entity = response.getEntity();
                String responseContent = EntityUtils.toString(entity, "UTF-8");
                if (responseContent != null) {
                    ret = JSONUtil.parseObj(responseContent);
                }
                ret.set("code", response.getStatusLine().getStatusCode());
                EntityUtils.consume(response.getEntity());
            }
            return ret;
        } catch (Exception e) {
            log.error("[Wechat-CLIENT] Error occur", e);
            return ret;
        }
    }

    public JSONObject sendPost(String path, String signRet, JSONObject bodyJson, long timestamp, String opId) {
        JSONObject ret = null;
        try {
            return ret;
        } catch (Exception e) {
            log.error("[Wechat-CLIENT] Error occur", e);
            return ret;
        }
    }

    private void addHeader(String signRet, long timestamp, HttpRequestBase base) {
//        base.addHeader("Content-Type", "application/json");
//        base.addHeader("X-Api-Key", bsnConfig.getApiKey());
//        base.addHeader("X-Timestamp", String.valueOf(timestamp));
//        base.addHeader("X-Signature", signRet);
    }

    public JSONObject sendDel(String path, String signRet, JSONObject bodyJson, long timestamp, String opIdStr) {
        JSONObject ret = null;
        try {
//            CloseableHttpResponse response = null;
//            HttpDelete httpDelete = new HttpDelete(bsnConfig.getHost() + path);
//            addHeader(signRet, timestamp, httpDelete);
//            //需要完成这个方法
////            StringEntity se = new StringEntity(bodyJson.toString(), ContentType.APPLICATION_JSON);
////            se.setContentEncoding("UFT-8");
////            httpDelete.setEntity(se);
//            response = client.execute(httpDelete);
//            if (response != null) {
//                HttpEntity entity = response.getEntity();
//                String responseContent = EntityUtils.toString(entity, "UTF-8");
//                int statusCode = response.getStatusLine().getStatusCode();
//
//                //回收链接到连接池
//                EntityUtils.consume(response.getEntity());
//            }
            return ret;
        } catch (Exception e) {
            log.error("[BSN-CLIENT] Error occur", e);
            return ret;
        }
    }

}
