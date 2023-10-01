package com.muling.mall.oms.service;

import com.huifu.adapay.core.exception.BaseAdaPayException;

import java.util.Map;

public interface IAdaPayService {

    /**
     * 交易关闭
     *
     * @param outTradeNo订单编号（唯一）
     */
    Map<String, Object> close(String outTradeNo) throws BaseAdaPayException;


}
