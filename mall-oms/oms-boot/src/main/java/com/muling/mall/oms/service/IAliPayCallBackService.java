package com.muling.mall.oms.service;

import com.alipay.api.AlipayApiException;

import java.util.Map;

public interface IAliPayCallBackService {

    /**
     * 处理阿里支付成功回调
     *
     * @param params
     * @throws AlipayApiException
     */
    void handleAliPayOrderNotify(Map<String, String> params) throws Exception;

}
