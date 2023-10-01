package com.muling.mall.oms.service;

import com.muling.mall.oms.pojo.dto.ApplePayCallBackDTO;
import com.muling.mall.oms.util.ApplePayCallBackResponse;

public interface IApplePayService {

    public void commit(ApplePayCallBackDTO callBackDTO, ApplePayCallBackResponse.InApp inApp);
}
