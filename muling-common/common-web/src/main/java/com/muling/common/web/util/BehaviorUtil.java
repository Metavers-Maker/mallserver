package com.muling.common.web.util;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
//import com.aliyuncs.DefaultAcsClient;
//import com.aliyuncs.IAcsClient;
//import com.aliyuncs.profile.DefaultProfile;
//import com.aliyuncs.profile.IClientProfile;
import com.muling.common.web.config.ValidationConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BehaviorUtil {
//    private final ValidationConfig validateionconfig;
    public static final String secretId = "9de19b53695c397ab400ac809242fc01";
    public static final String secretKey = "daca1cc44f78a9ed0cc8cc8e5a8417d5";

    private static final String captchaId = "79629bf97673434bb6c047c8b0b54f2c";

    private static final String version = "v2";

    public static String verifyApi = "http://c.dun.163yun.com/api/v2/verify"; // verify接口地址
    //
//    static String regionid = "cn-hangzhou";
//    static String accessKeyId = "*** Provide your AccessKeyId ***";
//    static String accessKeySecret = "*** Provide your AccessKeySecret ***";
//    static IClientProfile profile = DefaultProfile.getProfile(regionid, accessKeyId, accessKeySecret);
//    public static IAcsClient aliAcsClient = new DefaultAcsClient(profile);

    public static boolean verify(RestTemplate restTemplate, String validate, String user) {
        try {
            if (StringUtils.isEmpty(validate) || StringUtils.equals(validate, "null")) {
                return false;
            }
            user = (user == null) ? "" : user; // bugfix:如果user为null会出现签名错误的问题
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("captchaId", captchaId);
            params.put("validate", validate);
            params.put("user", user);
            // 公共参数
            params.put("secretId", secretId);
            params.put("version", version);
            params.put("timestamp", String.valueOf(System.currentTimeMillis()));
            params.put("nonce", RandomUtil.randomNumbers(4));
            // 计算请求参数签名信息
            String signature = sign(secretKey, params);
            params.put("signature", signature);

            HttpHeaders headers = new HttpHeaders();
            MediaType contentType = MediaType.parseMediaType("application/x-www-form-urlencoded;charset=utf-8");
            headers.setContentType(contentType);
            HttpEntity<String> request = new HttpEntity(headers);

            String url = verifyApi + "?captchaId={captchaId}&validate={validate}&user={user}&secretId={secretId}&version={version}&timestamp={timestamp}&nonce={nonce}&signature={signature}";
            ResponseEntity<String> result = restTemplate.postForEntity(url, request, String.class, params);
            String body = result.getBody();
            log.debug("行为验证结果：{}", body);
            VerifyResult verifyResult = verifyRet(body);

            return verifyResult.isResult();
        } catch (Exception e) {
            log.error("", e);
            throw e;
        }
    }

    /**
     * 生成签名信息
     *
     * @param secretKey 验证码私钥
     * @param params    接口请求参数名和参数值map，不包括signature参数名
     * @return
     */
    public static String sign(String secretKey, Map<String, String> params) {
        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        StringBuffer sb = new StringBuffer();
        for (String key : keys) {
            sb.append(key).append(params.get(key));
        }
        sb.append(secretKey);
        try {
            return DigestUtils.md5Hex(sb.toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();// 一般编码都支持的。。
        }
        return null;
    }

    /**
     * 验证返回结果<br>
     * 1. 当易盾服务端出现异常或者返回异常时，优先使用返回true的结果，反之阻塞用户的后序操作<br>
     * 2. 如果想修改为返回false结果。可以调用VerifyResult.fakeFalseResult(java.lang.String)函数
     *
     * @param resp
     * @return
     */
    private static VerifyResult verifyRet(String resp) {
        if (StringUtils.isEmpty(resp)) {
            return VerifyResult.fakeTrueResult("return empty response");
        }
        try {
            VerifyResult verifyResult = JSONUtil.toBean(resp, VerifyResult.class);
            return verifyResult;
        } catch (Exception ex) {
            log.error("yidun captcha return error response ,please check!");
            log.error("", ex);
            return VerifyResult.fakeTrueResult(resp);
        }
    }

    public static class VerifyResult {
        public VerifyResult() {
        }

        /**
         * 异常代号
         */
        private int error;
        /**
         * 错误描述信息
         */
        private String msg;
        /**
         * 二次校验结果 true:校验通过 false:校验失败
         */
        private boolean result;

        /**
         * 短信上行发送的手机号码
         * 仅限于短信上行的验证码类型
         */
        private String phone;

        /**
         * 额外字段
         */
        private String extraData;

        public int getError() {
            return error;
        }

        public void setError(int error) {
            this.error = error;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public boolean isResult() {
            return result;
        }

        public void setResult(boolean result) {
            this.result = result;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getExtraData() {
            return extraData;
        }

        public void setExtraData(String extraData) {
            this.extraData = extraData;
        }

        public static VerifyResult fakeFalseResult(String resp) {
            VerifyResult result = new VerifyResult();
            result.setResult(false);
            result.setError(0);
            result.setMsg(resp);
            result.setPhone("");
            return result;
        }

        public static VerifyResult fakeTrueResult(String resp) {
            VerifyResult result = new VerifyResult();
            result.setResult(true);
            result.setError(0);
            result.setMsg(resp);
            result.setPhone("");
            return result;
        }
    }

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        boolean er31 = BehaviorUtil.verify(restTemplate, "er31", "1234");
        System.out.println(er31);
    }
}
