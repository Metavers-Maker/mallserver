//
//  Created by  fred on 2017/1/12.
//  Copyright © 2016年 Alibaba. All rights reserved.
//

package com.muling.common.cert.service;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muling.common.cert.config.BSNConfig;
import com.muling.common.util.MD5Util;
import lombok.RequiredArgsConstructor;
import net.jodah.failsafe.RetryPolicy;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HttpApiClientBSN {

    private final BSNConfig bsnConfig;
    private final CloseableHttpClient client;

    private Logger log = LoggerFactory.getLogger(getClass());

    RetryPolicy<Object> retryPolicy = new RetryPolicy<>()
            .handle(Exception.class)
            .withDelay(Duration.ofSeconds(5))
            .withMaxRetries(3)
            .onFailedAttempt(e -> log.error("[HTTP-BSN] Fail to call BSN http client.", e.getLastFailure()))
            .onRetry(e -> log.warn("[HTTP-BSN] Failure #{}: trying again.", e.getAttemptCount()));


    public static final ObjectMapper mapper = new ObjectMapper();

    private static String sha256Sum(String str) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            // Should not happen
            e.printStackTrace();
        }
        byte[] encodedHash = digest.digest(str.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(encodedHash);
    }

    /**
     * 将 bytes 转为 Hex
     *
     * @param hash
     * @return
     */
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public String signBSN(String path, Map<String, String> queryParam, Map<String, String> bodyParam, String millTime) {

        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("path_url", path);
        if (queryParam != null && !queryParam.isEmpty()) {
            queryParam.forEach((key, value) -> paramsMap.put("query_" + key, value));
        }
        if (bodyParam != null && !bodyParam.isEmpty()) {
            bodyParam.forEach((key, value) -> paramsMap.put("body_" + key, value));
        }
        String jsonStr = JSON.toJSONString(paramsMap, SerializerFeature.MapSortField);
        // 执行签名
        String signature = sha256Sum(jsonStr + millTime + bsnConfig.getApiSc());
        return signature;
    }

    /**
     * Avatar-创建BSN账户
     */
    public JSONObject createAccount(String name, String opIdStr) {
        //注意长度为<=64
        String opId = MD5Util.encodeMD5(opIdStr);
        Map<String, String> bodyObj = new HashMap<>();
        bodyObj.put("name", name);
        bodyObj.put("operation_id", opId);
        JSONObject bodyJson = new JSONObject();
        bodyJson.putAll(bodyObj);
        //
        String path = "/v1beta1/account";
        long timestamp = System.currentTimeMillis();
        String signRet = this.signBSN(path, null, bodyObj, String.valueOf(timestamp));
        return this.sendPost(path, signRet, bodyJson, timestamp, opId);
    }

    public JSONObject queryBSN(String opIdStr) {
        //注意长度为<=64
        String path = "/v1beta1/tx/"+opIdStr;
        long timestamp = System.currentTimeMillis();
        String signRet = this.signBSN(path, null, null, String.valueOf(timestamp));
        return this.sendGet(path, signRet, null, timestamp, opIdStr);
    }


    /**
     * Avatar-创建NFT合约
     */
    public JSONObject createNftClass(
            String opIdStr,
            Long spuId,
            String nftName,
            String nftUrl,
            String nftSymbol,
            String nftOwner
    ) {
        //注意长度为<=64
        String opId = MD5Util.encodeMD5(opIdStr);
        if (nftOwner == null) {
            nftOwner = bsnConfig.getAccount();
        }
        //
        String classId = "class" + spuId.toString();
        Map<String, String> bodyObj = new LinkedHashMap<>();
        bodyObj.put("name", nftName);
        bodyObj.put("symbol", nftSymbol);
        bodyObj.put("owner", nftOwner);
        bodyObj.put("operation_id", opId);
        bodyObj.put("class_id", classId);
//        bodyObj.put("uri", nftUrl);
//        bodyObj.put("editable_by_class_owner", "1");
        JSONObject bodyJson = new JSONObject();
        bodyJson.putAll(bodyObj);
        //
        String path = "/v1beta1/nft/classes";
        long timestamp = System.currentTimeMillis();
        String signRet = this.signBSN(path, null, bodyObj, String.valueOf(timestamp));
        return this.sendPost(path, signRet, bodyJson, timestamp, opId);
    }

    /**
     * Avatar-查询NFT类别
     */
    public JSONObject queryNftClass(String opIdStr, Long spuId) {
        String classId = "class" + spuId.toString();
        String opId = MD5Util.encodeMD5(opIdStr);
        String path = "/v1beta1/nft/classes/" + classId;
        long timestamp = System.currentTimeMillis();
        String signRet = this.signBSN(path, null, null, String.valueOf(timestamp));
        return this.sendGet(path, signRet, null, timestamp, opId);
    }

    /**
     * Avatar-发行NFT
     */
    public JSONObject mintNFT(
            String opIdStr,
            Long spuId,
            String nftName,
            String nftUrl,
            String nftReceive
    ) {
        if (nftReceive == null) {
            nftReceive = bsnConfig.getAccount();
        }
        //注意长度为<=64
        String classId = "class" + spuId;
        String opId = MD5Util.encodeMD5(opIdStr);
        Map<String, String> bodyObj = new HashMap<>();
        bodyObj.put("name", nftName);
        bodyObj.put("uri", nftUrl);
        bodyObj.put("recipient", nftReceive);
        bodyObj.put("operation_id", opId);
        JSONObject bodyJson = new JSONObject();
        bodyJson.putAll(bodyObj);
        String path = "/v1beta1/nft/nfts/" + classId;
        long timestamp = System.currentTimeMillis();
        String signRet = this.signBSN(path, null, bodyObj, String.valueOf(timestamp));
        return this.sendPost(path, signRet, bodyJson, timestamp, opId);
    }

    /**
     * Avatar-转让NFT
     */
    public JSONObject transNFT(
            String opIdStr,
            Long spuId,
            String nftId,
            String nftOwner,
            String nftReceive
    ) {
        //注意长度为<=64
        String classId = "class" + spuId;
        String opId = MD5Util.encodeMD5(opIdStr);
        Map<String, String> bodyObj = new HashMap<>();
        bodyObj.put("recipient", nftReceive);
        bodyObj.put("operation_id", opId);
        JSONObject bodyJson = new JSONObject();
        bodyJson.putAll(bodyObj);
        String path = "/v1beta1/nft/nft-transfers/" + classId + "/" + nftOwner + "/" + nftId;
        long timestamp = System.currentTimeMillis();
        String signRet = this.signBSN(path, null, bodyObj, String.valueOf(timestamp));
        return this.sendPost(path, signRet, bodyJson, timestamp, opId);
    }

    /**
     * Avatar-销毁NFT
     */
    public JSONObject burnNFT(
            String opIdStr,
            String classId,
            String nftOwner,
            String nftId
    ) {
        //注意长度为<=64
        String opId = MD5Util.encodeMD5(opIdStr);
        Map<String, String> bodyObj = new HashMap<>();
        bodyObj.put("operation_id", opId);
        JSONObject bodyJson = new JSONObject();
        bodyJson.putAll(bodyObj);
        //https://stage.apis.avata.bianjie.ai/v2/nft/nfts/{class_id}/{owner}/{nft_id}
        String path = "/v1beta1/nft/nfts/" + classId + "/" + nftOwner + "/" + nftId;
        long timestamp = System.currentTimeMillis();
        String signRet = this.signBSN(path, null, bodyObj, String.valueOf(timestamp));
        return this.sendDel(path, signRet, bodyJson, timestamp, opId);
    }

    private void addHeader(String signRet, long timestamp, HttpRequestBase base) {
        base.addHeader("Content-Type", "application/json");
        base.addHeader("X-Api-Key", bsnConfig.getApiKey());
        base.addHeader("X-Timestamp", String.valueOf(timestamp));
        base.addHeader("X-Signature", signRet);
    }

    public JSONObject sendPost(String path, String signRet, JSONObject bodyJson, long timestamp, String opId) {
        JSONObject ret = null;
        try {
            HttpPost httpPost = new HttpPost(bsnConfig.getHost() + path);
            this.addHeader(signRet, timestamp, httpPost);
            if (bodyJson != null) {
                StringEntity se = new StringEntity(bodyJson.toString(), ContentType.APPLICATION_JSON);
                se.setContentEncoding("UFT-8");
                httpPost.setEntity(se);
            }
            CloseableHttpResponse response = client.execute(httpPost);
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
            log.error("[BSN-CLIENT] Error occur", e);
            return ret;
        }
    }

    public JSONObject sendGet(String path, String signRet, JSONObject bodyJson, long timestamp, String opId) {
        JSONObject ret = null;
        try {
            HttpGet httpGet = new HttpGet(bsnConfig.getHost() + path);
            this.addHeader(signRet, timestamp, httpGet);
            if (bodyJson != null) {
//                StringEntity se = new StringEntity(bodyJson.toString(), ContentType.APPLICATION_JSON);
//                se.setContentEncoding("UFT-8");
//                httpGet.setEntity(se);
            }
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
            log.error("[BSN-CLIENT] Error occur", e);
            return ret;
        }
    }

    public JSONObject sendDel(String path, String signRet, JSONObject bodyJson, long timestamp, String opIdStr) {
        JSONObject ret = null;
        try {
            CloseableHttpResponse response = null;
            HttpDelete httpDelete = new HttpDelete(bsnConfig.getHost() + path);
            this.addHeader(signRet, timestamp, httpDelete);
            //需要完成这个方法
//            StringEntity se = new StringEntity(bodyJson.toString(), ContentType.APPLICATION_JSON);
//            se.setContentEncoding("UFT-8");
//            httpDelete.setEntity(se);
            response = client.execute(httpDelete);
            if (response != null) {
                HttpEntity entity = response.getEntity();
                String responseContent = EntityUtils.toString(entity, "UTF-8");
                int statusCode = response.getStatusLine().getStatusCode();

                //回收链接到连接池
                EntityUtils.consume(response.getEntity());
            }

            return ret;
        } catch (Exception e) {
            log.error("[BSN-CLIENT] Error occur", e);
            return ret;
        }
    }
}
