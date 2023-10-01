package com.muling.mall.wms.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.muling.mall.wms.util.CheckSumBuilder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotifySmsService {

    private final String url = "https://api.netease.im/sms/sendtemplate.action";

    private final String appSecret = "86eaf8f7dbc8";

    private final String appKey = "7e6d922f8f50d6c8b205332c36bc193a";

    private final RestTemplate restTemplate;


    public boolean sendNotify(Integer type, String phoneNumber, String params) throws Exception {
        try {
            String curTime = String.valueOf((new Date()).getTime() / 1000L);
            String nonce = RandomUtil.randomNumbers(4); // 随机生成4位的验证码
            /*
             * 参考计算CheckSum的java代码，在上述文档的参数列表中，有CheckSum的计算文档示例
             */
            String checkSum = CheckSumBuilder.getCheckSum(appSecret, nonce, curTime);

            String templateId = "";
            // 根据type是对应templateId
            if (type == 0) {
                templateId = "19522853";
            } else if (type == 1) {
                templateId = "19522058";
            }

            MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
            map.add("templateid", templateId);
            map.add("mobiles", phoneNumber);
//            map.add("params", params);

            HttpHeaders headers = new HttpHeaders();
            MediaType contentType = MediaType.parseMediaType("application/x-www-form-urlencoded;charset=utf-8");
            headers.setContentType(contentType);
            headers.add("AppKey", appKey);
            headers.add("Nonce", nonce);
            headers.add("CurTime", curTime);
            headers.add("CheckSum", checkSum);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
            ResponseEntity<YunXinResponse> result = restTemplate.postForEntity(url, request, YunXinResponse.class);
            log.info("发送短信结果：{}.{}.{}", phoneNumber, params, JSONUtil.toJsonStr(result));
            YunXinResponse body = result.getBody();
            return "200".equals(body.getCode());
        } catch (Exception e) {
            log.error("发送短信异常", e);
            throw e;
        }
    }

    @Data
    public static class YunXinResponse {
        private String code;
        private String msg;
        private String desc;

    }
}
