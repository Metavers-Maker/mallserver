package com.muling.common.web.encrypt.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muling.common.result.ResultCode;
import com.muling.common.web.encrypt.base.ResponseBasic;
import com.muling.common.web.encrypt.base.SecretResponseBasic;
import com.muling.common.web.encrypt.filter.SecretFilter;
import com.muling.common.web.util.EncryptUtils;
import com.muling.common.web.util.Md5Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.Resource;

@Slf4j
@ControllerAdvice
public class SecretResponseAdvice implements ResponseBodyAdvice {
    @Resource
    private ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        // 判断是否需要加密
        Boolean respSecret = SecretFilter.secretThreadLocal.get();
        String secretKey = SecretFilter.clientPrivateKeyThreadLocal.get();
        // 清理本地缓存
        SecretFilter.secretThreadLocal.remove();
        SecretFilter.clientPrivateKeyThreadLocal.remove();
        if (null != respSecret && respSecret) {
            if (o instanceof ResponseBasic) {
                // 外层加密级异常
                if (ResultCode.ERROR.getCode().equals(((ResponseBasic) o).getCode())) {
                    return SecretResponseBasic.fail(((ResponseBasic) o).getCode(), ((ResponseBasic) o).getData(), ((ResponseBasic) o).getMsg());
                }
                // 业务逻辑
                try {
                    // 使用FastJson序列号会导致和之前的接口响应参数不一致，后面会重点讲到
                    String data = EncryptUtils.aesEncrypt(objectMapper.writeValueAsString(o), secretKey);
                    // 增加签名
                    long timestamp = System.currentTimeMillis() / 1000;
                    int salt = EncryptUtils.genSalt();
                    String dataNew = timestamp + "" + salt + "" + data + secretKey;
                    String newSignature = Md5Utils.genSignature(dataNew);
                    return SecretResponseBasic.success(data, timestamp, salt, newSignature);
                } catch (Exception e) {
                    log.error("beforeBodyWrite error:", e);
                    return SecretResponseBasic.fail(400, "", "服务端处理结果数据异常");
                }
            }
        }
        return o;
    }
}
