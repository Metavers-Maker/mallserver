package com.muling.mall.oms.service;

import com.alipay.api.AlipayApiException;

import java.util.Map;

public interface IAdaPayCallBackService {

    /**
     * 处理ADA支付成功回调
     *
     * @param data
     * @throws Exception
     */
    void handleAdaPayOrderNotify(String data) throws Exception;

}
