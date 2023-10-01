package com.muling.mall.oms.controller.app;

import com.github.binarywang.wxpay.exception.WxPayException;
import com.huifu.adapay.core.AdapayCore;
import com.huifu.adapay.core.util.AdapaySign;
import com.muling.mall.oms.service.IAdaPayCallBackService;
import com.muling.mall.oms.service.IAliPayCallBackService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * ADA回调接口
 *
 * @author Gadfly
 * @since 2021-05-27 14:24
 */
@Api(tags = "app-ADA支付回调接口")
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/callback-api/v1/ada-pay")
public class AdaPayCallbackController {


    private final IAdaPayCallBackService adaPayCallBackService;

    /**
     * ADA支付结果回调
     *
     * @param request
     * @return
     * @throws WxPayException
     */
    @PostMapping(value = "/notify-order", produces = MediaType.TEXT_PLAIN_VALUE)
    public void adaPayOrderNotify(HttpServletRequest request) {
        log.info("ADA开始处理支付结果通知");
        try {
            //验签请参data
            String data = request.getParameter("data");
            //验签请参sign
            String sign = request.getParameter("sign");
            //验签标记
            boolean checkSign;
            //验签请参publicKey
            String publicKey = AdapayCore.PUBLIC_KEY;
            log.info("ADA验签请参：data={}sign={}");
            //验签
            checkSign = AdapaySign.verifySign(data, sign, publicKey);
            if (checkSign) {
                //验签成功逻辑
                System.out.println("ADA成功返回数据data:" + data);
            } else {
                //验签失败逻辑
            }
            log.info("ADA支付通知成功:[{}]", data);
            adaPayCallBackService.handleAdaPayOrderNotify(data);

        } catch (Exception e) {
            log.error("ADA回调处理异常", e);
        }
    }

}
