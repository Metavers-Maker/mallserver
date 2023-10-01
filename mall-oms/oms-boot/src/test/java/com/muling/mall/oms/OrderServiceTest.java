package com.muling.mall.oms;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.muling.common.constant.RedisConstants;
import com.muling.common.exception.BizException;
import com.muling.common.result.ResultCode;
import com.muling.mall.oms.config.ApplePayConfig;
import com.muling.mall.oms.mapper.OrderMapper;
import com.muling.mall.oms.pojo.dto.ApplePayCallBackDTO;
import com.muling.mall.oms.pojo.entity.OmsOrder;
import com.muling.mall.oms.service.IAdaPayCallBackService;
import com.muling.mall.oms.service.IAliPayCallBackService;
import com.muling.mall.oms.util.ApplePayCallBackResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by Given on 2021/12/6
 */
@SpringBootTest
@Slf4j
public class OrderServiceTest {


    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private IAliPayCallBackService aliPayCallBackService;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private ApplePayConfig applePayConfig;

    @Test
    void authTest() {
        String s = stringRedisTemplate.opsForValue().get(RedisConstants.UMS_AUTH_SUFFIX + 2L);
        if (StrUtil.isBlank(s) || !s.equals("1")) {
            throw new BizException(ResultCode.USER_AUTH_NOT_EXIST);
        }
    }

    @Test
    void test() throws Exception {
        String msg = "{\"gmt_create\":\"2022-06-02 12:02:13\",\"charset\":\"UTF-8\",\"seller_email\":\"xrmeta@xrsd.com.cn\",\"subject\":\"商品名称\",\"sign\":\"i+Iu2F4+poNgew/AYGxngNOsOUEMMevMBExohXYfaivrXxvYRdIRqhEKJgq2wk8Go5tqgU9MV6pLgo6fwXAjj9T7d6kOs1ih5pN0uP1dPq2wf6Ptyt/j83qOnjFd1ZeJ9KEy1QVIZ/xw18ph6Y1f52oZ9foBmDW2JSWZFQMuqupcEpsc+18Mgq/ED7iNbYgyH/DSzVs5A8uiZNAqnlo2dhC+CCkVZRSH6EK3DT5FsU11VNbWbVmQc0AIRtpSsjXAr2pEddgA/ZMXLJddm3Iu+L7P6M" +
                "PvXb8ROIZ9N1Y58G5Gf3WeUWoUAyi6leRDd5PdKX9jk7cJsJH76xqrhvR9KA==\",\"body\":\"赅买-订单编号20220602300000054\",\"buyer_id\":\"2088002316456021\",\"invoice_amount\":\"0.01\",\"notify_id\":\"2022060200222120215056021443681702\",\"fund_bill_list\":\"[{\\\"amount\\\":\\\"0.01\\\",\\\"fundChannel\\\":\\\"ALIPAYACCOUNT\\\"}]\",\"notify_type\":\"trade_status_sync\",\"trade_status\":\"TRADE_SUCCESS\",\"receipt_amount\":\"0.01\",\"app_id\":\"2021003129684039\",\"buyer_pay_amount\":\"0.01\",\"sign_type\":\"RSA2\",\"seller_id\":\"2088441335878537\",\"gmt_payment\":\"2022-06-02 12:02:14\"" +
                ",\"notify_time\":\"2022-06-02 12:02:15\",\"version\":\"1.0\",\"out_trade_no\":\"alio_165414252520165303360\",\"total_amount\":\"0.01\",\"trade_no\":\"2022060222001456021400494235\",\"auth_app_id\":\"2021003129684039\",\"buyer_logon_id\":\"457***@qq.com\",\"point_amount\":\"0.00\"}";
        Map<String, String> maps = (Map) JSON.parse(msg);
        try {
            aliPayCallBackService.handleAliPayOrderNotify(maps);
        } catch (Exception e) {
            log.error("支付宝回调处理异常：{}", e);
        }
    }

    @Test
    void applePayTest() {
        String data = "{\"orderId\":36,\"outTradeNo\":\"apo_165423624660692600006\",\"receipt\":\"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\",\"transactionId\":2000000070904793}";
        String id = "com.xrtime.ywzm_01";

        ApplePayCallBackDTO applePayCallBackDTO = JSONUtil.toBean(data, ApplePayCallBackDTO.class);

        Map<String, String> params = new HashedMap();
        params.put("receipt-data", applePayCallBackDTO.getReceipt());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(JSONUtil.toJsonStr(params), headers);
        ResponseEntity<ApplePayCallBackResponse> result = restTemplate.postForEntity(applePayConfig.getApplePayUrl(), entity, ApplePayCallBackResponse.class);
        System.out.println(result.getBody());
    }

    @Resource
    private OrderMapper orderMapper;

    @Test
    void ssss() {
        OmsOrder order = orderMapper.findById(1L);
        System.out.println(order);
    }

    @Test
    void ddddd() {
        OmsOrder order = orderMapper.findByOrderSn("20220605300000029");
        System.out.println(order);
    }

    @Resource
    private IAdaPayCallBackService adaPayCallBackService;

    @Test
    void eeeee() throws Exception {
        String data = "{\"order_no\":\"ado_165596625883716500067\",\"created_time\":\"20220623143740\",\"pay_amt\":\"0.01\",\"end_time\":\"20220623143751\",\"description\":\"\",\"out_trans_id\":\"2022062322001456021422529019\",\"party_order_id\":\"02212206235265976403260\",\"expend\":{\"sub_open_id\":\"2088002316456021\"},\"pay_channel\":\"alipay_qr\",\"id\":\"002112022062314373910385934619757092864\",\"app_id\":\"app_74b5d3d1-7482-48c6-903e-8f3ae2461430\",\"fee_amt\":\"0.00\",\"status\":\"succeeded\"}";
        adaPayCallBackService.handleAdaPayOrderNotify(data);
    }

    @Test
    void fffff() throws Exception {
//        BankBindForm form = new BankBindForm();
//        form.setMobile("13810317769");
//        form.setCardType("1");
//        form.setCardNo("6225880172842412");
//        form.setCardBank("");
//        sandService.bindBankCard(form, true);
//        sandService.queryBankCard();
    }

}
