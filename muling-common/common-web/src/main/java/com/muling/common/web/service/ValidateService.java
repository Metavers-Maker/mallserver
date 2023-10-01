package com.muling.common.web.service;

import com.aliyuncs.IAcsClient;
import com.aliyuncs.afs.model.v20180112.AuthenticateSigRequest;
import com.aliyuncs.afs.model.v20180112.AuthenticateSigResponse;
import com.muling.common.web.config.ValidationConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

//import org.redisson.api.RLock;
//import org.redisson.api.RedissonClient;

@Service
@Slf4j
public class ValidateService {

    @Autowired
    private ValidationConfig validationConfig;

//    private final StringRedisTemplate stringRedisTemplate;
//
//    private final RestTemplate restTemplate;
//
    @Autowired
    @Qualifier("validateIAcsClient")
    private IAcsClient client;

    public boolean validateByALi(String session, String sig, String token) throws Exception {

        try {
            AuthenticateSigRequest request = new AuthenticateSigRequest();
            request.setSessionId(session);// 会话ID。必填参数，从前端sucess回调中获取，不可更改。
            request.setSig(sig);// 签名串。必填参数，从前端sucess回调中获取，不可更改。
            request.setToken(token);// 请求唯一标识。必填参数，从前端sucess回调中获取，不可更改。
            request.setScene(validationConfig.getAppScene());// 场景标识。必填参数，与前端页面填写数据一致，不可更改。
            request.setAppKey(validationConfig.getAppKey());// 应用类型标识。必填参数，后端填写。
            request.setRemoteIp("xxx");// 客户端IP。必填参数，后端填写。
            try {
                //response的code枚举：100验签通过，900验签失败。
                AuthenticateSigResponse response = client.getAcsResponse(request);
                // TODO
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        } catch (Exception e) {
            log.error("", e);
            throw e;
        }

//        String content = "【元物之门】验证码：" + code + "（有效期为5分钟），为保证账户安全，请勿将验证码提供给他人，若非本人操作请忽略。";
//        String ts = String.valueOf(System.currentTimeMillis());
//        String md5 = SecureUtil.md5(smsConfig.getAllNet().getUserId() + ts + smsConfig.getAllNet().getApiKey());
//
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
//        params.add("userid", smsConfig.getAllNet().getUserId());
//        params.add("ts", ts);
//        params.add("sign", md5.toLowerCase());
//        params.add("mobile", phoneNumber);
//        params.add("msgcontent", content);
//        params.add("extnum", "");
//        try {
//            ResponseEntity<AllNetResponse> result = restTemplate.postForEntity(smsConfig.getAllNet().getUrl(), params, AllNetResponse.class);
//            log.info("发送短信结果：{}.{}.{}", phoneNumber, code, JSONUtil.toJsonStr(result));
//            AllNetResponse body = result.getBody();
//            return "0".equals(body.getCode());
//        } catch (Exception e) {
//            log.error("", e);
//            throw e;
//        }
    }


}
