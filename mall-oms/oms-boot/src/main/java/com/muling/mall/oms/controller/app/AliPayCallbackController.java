package com.muling.mall.oms.controller.app;

import com.alibaba.fastjson.JSON;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.google.common.collect.Maps;
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
import java.util.Iterator;
import java.util.Map;

/**
 * 阿里回调接口
 *
 * @author Gadfly
 * @since 2021-05-27 14:24
 */
@Api(tags = "app-阿里支付回调接口")
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/callback-api/v1/ali-pay")
public class AliPayCallbackController {


    private final IAliPayCallBackService aliPayCallBackService;

    /**
     * 阿里下单支付结果回调
     *
     * @param request
     * @return
     * @throws WxPayException
     */
    @PostMapping(value = "/notify-order", produces = MediaType.TEXT_PLAIN_VALUE)
    public String aliPayOrderNotify(HttpServletRequest request) {
        log.info("开始处理支付结果通知");
        try {
            Map requestParams = request.getParameterMap();
            //获取支付宝POST过来反馈信息
            Map<String, String> params = Maps.newHashMap();
            for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                //乱码解决，这段代码在出现乱码时使用。
                //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
                params.put(name, valueStr);
            }

            log.info("支付通知成功:[{}]", JSON.toJSONString(params));
            aliPayCallBackService.handleAliPayOrderNotify(params);

        } catch (Exception e) {
            log.error("支付宝回调处理异常", e);
            return "fail";
        }
        //成功
        return "success";
    }

}
