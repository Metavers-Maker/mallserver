package com.muling.mall.ums.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.muling.mall.ums.config.AuthConfig;
import com.muling.mall.ums.service.IAuthService;
import com.muling.mall.ums.util.DesEncrypter;
import com.muling.mall.ums.util.ResponseAuth;
import com.muling.mall.ums.util.ResponseDTO;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final RestTemplate restTemplate;

    private final AuthConfig authConfig;

    private final DesEncrypter desEncrypter;


    public ResponseAuth auth(String name, String idCard, String mobile) throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "APPCODE " + authConfig.appCode);
        headers.add("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        //body param
        MultiValueMap<String, String> bodys = new LinkedMultiValueMap<String, String>();
        bodys.add("idCard", idCard);
        bodys.add("mobile", mobile);
        bodys.add("realName", name);
        HttpEntity<MultiValueMap<String, String>> formEntity = new HttpEntity<MultiValueMap<String, String>>(bodys, headers);
        //发送http
        ResponseEntity<String> result = restTemplate.postForEntity(authConfig.host, formEntity, String.class);
        log.info("auth responseEntity----" + result);
        ResponseAuth responseAuth = JSONUtil.toBean(result.getBody(), ResponseAuth.class);
        return responseAuth;
    }

    /**
     * 是通科技 三要素认证
     * */
    public ResponseDTO authEx(String name, String idCard, String mobile) throws Exception {
        String apiKey = authConfig.apiKey;
        String param = "name=" + name + "&idCard=" + idCard + "&mobile=" + mobile;
        String encryptParam = desEncrypter.encrypt(param);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.add("rettype", "json");
        params.add("encryptParam", encryptParam);
        ResponseEntity<String> result = restTemplate.postForEntity(authConfig.url + apiKey, params, String.class);
        log.info("response", result.getBody());
        ResponseDTO responseDTO = JSONUtil.toBean(result.getBody(), ResponseDTO.class);
        return responseDTO;
    }
}
