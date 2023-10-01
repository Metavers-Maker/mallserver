package com.muling.mall.bms.controller.app;

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
 * BSN回调接口
 *
 * @author freeze
 */

@Api(tags = "app-BSN文昌链回调接口")
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/callback-api/v1/bsn-avatar")
public class BsnCallbackController {

//    private final IAdaPayCallBackService adaPayCallBackService;

    /**
     * BSN结果回调
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/notify-nft", produces = MediaType.TEXT_PLAIN_VALUE)
    public void nftNotify(HttpServletRequest request) {
        log.info("BSN链NFT交易结果通知 {}", request);
        try {
//            //验签请参data
//            String data = request.getParameter("data");
//            //验签请参sign
//            String sign = request.getParameter("sign");
//            //验签标记
//            boolean checkSign;
//            //验签请参publicKey
//            String publicKey = AdapayCore.PUBLIC_KEY;
//            log.info("ADA验签请参：data={}sign={}");
//            //验签
//            checkSign = AdapaySign.verifySign(data, sign, publicKey);
//            if (checkSign) {
//                //验签成功逻辑
//                System.out.println("ADA成功返回数据data:" + data);
//            } else {
//                //验签失败逻辑
//            }
//            log.info("ADA支付通知成功:[{}]", data);
//            adaPayCallBackService.handleAdaPayOrderNotify(data);

        } catch (Exception e) {
            log.error("BSN链NFT交易转移处理异常", e);
        }
    }

}
