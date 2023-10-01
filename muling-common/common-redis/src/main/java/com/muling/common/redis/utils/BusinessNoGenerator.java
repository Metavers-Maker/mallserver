package com.muling.common.redis.utils;

import com.muling.common.constant.RedisConstants;
import com.muling.common.enums.BusinessTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;


@Component
@Slf4j
public class BusinessNoGenerator {

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * @param businessType 业务类型枚举
     * @param digit        业务序号位数
     * @return
     */
    public String generate(BusinessTypeEnum businessType, Integer digit) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String date = LocalDateTime.now(ZoneOffset.of("+8")).format(formatter);
        String key = RedisConstants.BUSINESS_NO_PREFIX + businessType.getValue() + ":" + date;
        Long increment = redisTemplate.opsForValue().increment(key);
        return date + businessType.getValue() + String.format("%0" + digit + "d", increment);
    }


    public String generate(BusinessTypeEnum businessType) {
        Integer defaultDigit = 6;
        return generate(businessType, defaultDigit);
    }

    public Long generateLong(BusinessTypeEnum businessType) {
        Integer defaultDigit = 6;
        return Long.valueOf(generate(businessType, defaultDigit));
    }

}
