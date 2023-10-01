package com.muling.common.util;

import cn.hutool.core.util.StrUtil;
import com.muling.common.constant.SecurityConstants;
import com.muling.common.enums.VCodeTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

@Slf4j
public class VCodeUtils {

    public static String getCode(StringRedisTemplate stringRedisTemplate, VCodeTypeEnum value, String mobile) {
        String code = stringRedisTemplate.opsForValue().get(SecurityConstants.SMS_CODE_PREFIX + value.getValue() + mobile);
        return code;
    }

    public static void setCode(StringRedisTemplate stringRedisTemplate, VCodeTypeEnum value, String mobile, String code) {
        stringRedisTemplate.opsForValue().set(SecurityConstants.SMS_CODE_PREFIX + value.getValue() + mobile, code, 300, TimeUnit.SECONDS);
    }

    public static boolean checkVCode(StringRedisTemplate stringRedisTemplate, VCodeTypeEnum value, String mobile, String code) {
        String correctCode = SecurityConstants.SMS_CODE_PREFIX + value.getValue() + mobile;
        String sCode = stringRedisTemplate.opsForValue().get(correctCode);
        log.info(value.getLabel() + "验证-手机:{}，验证码:{}，输入码:{}", mobile, sCode, code);
        if (StrUtil.isBlank(sCode) || !sCode.equals(code)) {
            return false;
        } else {
            stringRedisTemplate.delete(correctCode);
            return true;
        }
    }

    public static boolean delete(StringRedisTemplate stringRedisTemplate, VCodeTypeEnum value, String mobile) {
        String correctCode = SecurityConstants.SMS_CODE_PREFIX + value.getValue() + mobile;
        Boolean delete = stringRedisTemplate.delete(correctCode);
        return delete;
    }

}
