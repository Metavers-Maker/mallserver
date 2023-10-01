package com.muling.mall.oms.service.impl;

import com.huifu.adapay.core.exception.BaseAdaPayException;
import com.huifu.adapay.model.Payment;
import com.muling.mall.oms.service.IAdaPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdaPayServiceImpl implements IAdaPayService {


    /**
     * 交易关闭
     *
     * @param outTradeNo 订单编号（唯一）
     */
    @Override
    public Map<String, Object> close(String outTradeNo) throws BaseAdaPayException {
        Map<String, Object> paymentParams = new HashMap<>(10);
        paymentParams.put("payment_id", outTradeNo);
        paymentParams.put("reason", "reason");
        paymentParams.put("expend", "expend");
        paymentParams.put("notify_url", "notify_url");
        Map<String, Object> close = Payment.close(paymentParams);

        return close;
    }


}
