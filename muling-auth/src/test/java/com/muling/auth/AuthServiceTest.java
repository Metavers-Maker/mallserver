package com.muling.auth;

import cn.hutool.json.JSONObject;
import com.muling.common.cert.service.HttpApiClientWechat;
import com.muling.common.constant.RedisConstants;
import com.muling.common.sms.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.net.URISyntaxException;

/**
 * Created by Given on 2021/12/6
 */
@SpringBootTest
@Slf4j
public class AuthServiceTest {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private SmsService smsService;

    @Resource
    private HttpApiClientWechat httpApiClientWechat;

    @Test
    void authTest() {
        stringRedisTemplate.opsForValue().set(RedisConstants.UMS_AUTH_SUFFIX + 2L, "1");
    }

    @Test
    void test() throws URISyntaxException {
//        JSONObject ret = httpApiClientWechat.openLogin("abc");
        smsService.sendSmsCode(5,"13810317769");
    }

    public static void main(String[] args) {

    }

}
