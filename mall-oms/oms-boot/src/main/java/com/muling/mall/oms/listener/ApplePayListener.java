package com.muling.mall.oms.listener;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.muling.common.constant.GlobalConstants;
import com.muling.mall.oms.config.ApplePayConfig;
import com.muling.mall.oms.constant.OmsConstants;
import com.muling.mall.oms.pojo.dto.ApplePayCallBackDTO;
import com.muling.mall.oms.service.IApplePayService;
import com.muling.mall.oms.util.ApplePayCallBackResponse;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApplePayListener {

    private final RestTemplate restTemplate;

    private final RedissonClient redissonClient;

    private final IApplePayService applePayService;


    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(value = GlobalConstants.MQ_APPLE_PAY_QUEUE, durable = "true"),
                    exchange = @Exchange(value = GlobalConstants.MQ_APPLE_PAY_EXCHANGE),
                    key = GlobalConstants.MQ_APPLE_PAY_KEY
            )
    })
    @RabbitHandler
    public void process(String data, Message message, Channel channel) throws Exception {
        log.info("苹果支付日志：{}", data);
        try {
            ApplePayCallBackDTO callBackDTO = JSONUtil.toBean(data, ApplePayCallBackDTO.class);

            RLock lock = redissonClient.getLock(OmsConstants.PAY_CALLBACK_PREFIX + callBackDTO.getTransactionId());

            try {
                lock.lock();

                Map<String, String> params = new HashedMap();
                params.put("receipt-data", callBackDTO.getReceipt());

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> entity = new HttpEntity<>(JSONUtil.toJsonStr(params), headers);
                ResponseEntity<ApplePayCallBackResponse> result = restTemplate.postForEntity(ApplePayConfig.OFFICAL_URL, entity, ApplePayCallBackResponse.class);
                ApplePayCallBackResponse response = result.getBody();
                log.info("苹果支付日志响应:{}.{}", data, JSONUtil.toJsonStr(response));
                if (response.getStatus() == 0) {
                    applePayService.commit(callBackDTO, response.getReceipt().getIn_app().get(0));
                } else if (response.getStatus().intValue() == 21007) {
                    ResponseEntity<ApplePayCallBackResponse> sandBoxResult = restTemplate.postForEntity(ApplePayConfig.SAND_BOX_URL, entity, ApplePayCallBackResponse.class);
                    ApplePayCallBackResponse sandBoxResponse = sandBoxResult.getBody();
                    log.info("苹果支付日志响应:{}.{}", data, JSONUtil.toJsonStr(sandBoxResult));
                    if (sandBoxResponse.getStatus() == 0) {
                        applePayService.commit(callBackDTO, sandBoxResponse.getReceipt().getIn_app().get(0));
                    } else {
                        log.error("沙箱支付失败:{}.{}", data, JSONUtil.toJsonStr(sandBoxResponse));
                    }
                } else {
                    log.error("苹果支付结果通知失败:{}.{}", data, JSON.toJSONString(response));
                }
            } finally {
                //释放锁
                if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } catch (Exception e) {
            log.error("苹果回调处理异常", e);
            throw e;
        }
    }
}
