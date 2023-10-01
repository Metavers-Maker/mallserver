package com.muling.mall.oms.controller.app;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.net.HttpHeaders;
import com.muling.common.cert.util.sand.CertUtil;
import com.muling.common.cert.util.sand.SignatureUtils;
import com.muling.mall.oms.enums.OrderStatusEnum;
import com.muling.mall.oms.pojo.entity.OmsOrder;
import com.muling.mall.oms.service.IOrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/sand")
@Tag(name = "支付相关接口")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class SandPayController {

    private final IOrderService orderService;

    /**
     * sand支付回调
     *
     * @param req  请求
     * @param resp 响应
     */
    @RequestMapping("/pay/callback")
    public String payCallBack(HttpServletRequest req, HttpServletResponse resp) {
        try {
            Map<String, String[]> parameterMap = req.getParameterMap();
            if (parameterMap != null && !parameterMap.isEmpty()) {
                String data = req.getParameter("data");
                String sign = req.getParameter("sign");
                String signType = req.getParameter("signType");
                // 验证签名
                log.info("sand_pay 回调结果 data:{} ,sign:{} signType:{}", data, sign, signType);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("data", data);
                jsonObject.put("sign", sign);
                jsonObject.put("signType", signType);
                boolean verify = SignatureUtils.verify(data, sign, signType, CertUtil.getPublicKey());
                log.info("sand_pay回调验签：{}", verify);
                if (verify) {
                    JSONObject jsonData = JSON.parseObject(data);
                    String respCode = jsonData.getString("respCode");
                    String orderStatus = jsonData.getString("orderStatus");
                    if ("00000".equals(respCode) && "00".equals(orderStatus)) {
                        String orderNo = jsonData.getString("orderNo");
                        OmsOrder nftOrder = orderService.findByOrderSn(orderNo);
                        if (nftOrder.getStatus() == OrderStatusEnum.PENDING_PAYMENT.getValue()) {
                            log.info("支付成功，订单状态正常：{}", JSON.toJSONString(nftOrder));
                            orderService.payOrderSuccess(nftOrder);
                        } else {
                            log.info("支付成功，被关闭回调结果订单已：{}", JSON.toJSONString(nftOrder));
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.info("sand回调异常", e);
        }
        return "respCode=000000";
    }

    /**
     * sand交易回调
     */
    @RequestMapping("/trade/callback")
    public String tradeCallBack(HttpServletRequest req, HttpServletResponse resp) {
        try {
            Map<String, String[]> parameterMap = req.getParameterMap();
            if (parameterMap != null && !parameterMap.isEmpty()) {
                String data = req.getParameter("data");
                String sign = req.getParameter("sign");
                String signType = req.getParameter("signType");
                // 验证签名
                log.info("sand_trade 回调结果 data:{} ,sign:{} signType:{}", data, sign, signType);
                boolean verify = SignatureUtils.verify(data, sign, signType, CertUtil.getPublicKey());
//                boolean verify = CryptoUtil.verifyDigitalSign(data.getBytes("UTF-8"), Base64.decodeBase64(sign), CertUtil.getPublicKey(), "SHA1WithRSA");
                log.info("sand_trade 回调验签：{}", verify);
                if (verify) {
                    JSONObject jsonObject = JSON.parseObject(data);
                    JSONObject head = jsonObject.getJSONObject("head");
                    String respCode = head.getString("respCode");
                    if ("000000".equals(respCode)) {
                        JSONObject body = jsonObject.getJSONObject("body");
                        String orderStatus = body.getString("orderStatus");
                        String orderNo = body.getString("orderCode");
                        if ("1".equals(orderStatus)) {
                            OmsOrder nftOrder = orderService.findByOrderSn(orderNo);
                            if (nftOrder.getStatus() == OrderStatusEnum.PENDING_PAYMENT.getValue()) {
                                log.info("支付成功，订单状态正常：{}", JSON.toJSONString(nftOrder));
                                orderService.payOrderSuccess(nftOrder);
                            } else {
                                log.info("支付成功，被关闭回调结果订单已：{}", JSON.toJSONString(nftOrder));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.info("sand回调异常", e);
        }
        return "respCode=000000";
    }

//    /**
//     * @param req  请求
//     * @param resp 响应
//     */
//    @RequestMapping("/callback")
//    public String callBack(HttpServletRequest req, HttpServletResponse resp) {
//        try {
//            Map<String, String[]> parameterMap = req.getParameterMap();
//            if (parameterMap != null && !parameterMap.isEmpty()) {
//                String data = req.getParameter("data");
//                String sign = req.getParameter("sign");
//                String signType = req.getParameter("signType");
//                // 验证签名
//                log.info("sand回调结果 data:{} ,sign:{} signType:{}", data, sign, signType);
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("data", data);
//                jsonObject.put("sign", sign);
//                jsonObject.put("signType", signType);
//                //
//                boolean verify = SignatureUtils.verify(data, sign, signType, CertUtil.getPublicKey());
//                log.info("sand回调验签：{}", verify);
//                if (verify) {
//                    JSONObject object = JSON.parseObject(data);
//                    String bizType = object.getString("bizType");
//                    String respCode = object.getString("respCode");
//                    if ("SIGN_PROTOCOL".equals(bizType) && "00000".equals(respCode)) {
//                        Long bizUserNo = object.getLong("bizUserNo");
//                        String masterAccount = object.getString("masterAccount");
//                        MemberSandDTO memberSandDTO = new MemberSandDTO();
//                        memberSandDTO.setMemberId(bizUserNo);
//                        memberSandDTO.setSandId(masterAccount);
//                        memberFeignClient.addSandAccount(memberSandDTO);
//                    }
//                }
//
//            }
//        } catch (Exception e) {
//            log.info("sand回调异常", e);
//        }
//        return "respCode=000000";
//    }

    public static void main(String[] args) throws Exception {
//        String ss = "{\"bizType\":\"SIGN_PROTOCOL\",\"orderNo\":\"2023020615505506163383\",\"masterAccount\":\"200734000010009\",\"respTime\":\"20230206155211\",\"bizUserNo\":\"1622502746791538688\",\"mid\":\"6888800118807\",\"respMsg\":\"协议签约操作成功\",\"respCode\":\"00000\",\"signProtocolInfo\":[{\"signStatus\":\"01\",\"protocolNo\":\"XY001\"}]}";
//        JSONObject jsonObject = JSON.parseObject(ss);
//        String bizType = jsonObject.getString("bizType");
//        String respCode = jsonObject.getString("respCode");
//        if ("SIGN_PROTOCOL".equals(bizType) && "00000".equals(respCode)) {
//            Long bizUserNo = jsonObject.getLong("bizUserNo");
//            Long masterAccount = jsonObject.getLong("masterAccount");
//            System.out.println(bizUserNo + "   " + masterAccount);
//        }

//        String ff = "{\"head\":{\"version\":\"1.0\",\"respTime\":\"20230207092421\",\"respCode\":\"000000\",\"respMsg\":\"成功\"},\"body\":{\"mid\":\"6888800118807\",\"orderCode\":\"1622767633929302016\",\"tradeNo\":\"1622767633929302016\",\"clearDate\":\"20230207\",\"totalAmount\":\"000000000100\",\"orderStatus\":\"1\",\"payTime\":\"20230207092421\",\"settleAmount\":\"000000000100\",\"buyerPayAmount\":\"000000000000\",\"discAmount\":\"000000000000\",\"txnCompleteTime\":\"\",\"payOrderCode\":\"20230207sdb47310000000561\",\"accLogonNo\":\"\",\"accNo\":\"\",\"midFee\":\"000000000010\",\"extraFee\":\"000000000000\",\"specialFee\":\"000000000000\",\"plMidFee\":\"000000000000\",\"bankserial\":\"\",\"externalProductCode\":\"00002046\",\"cardNo\":\"\",\"creditFlag\":\"\",\"bid\":\"\",\"benefitAmount\":\"000000000000\",\"remittanceCode\":\"\",\"extend\":\"\",\"accountAmt\":\"000000000100\",\"masterAccount\":\"200734000070006\"}}";
//
//        JSONObject jsonObject = JSON.parseObject(ff);
//
//        JSONObject head = jsonObject.getJSONObject("head");
//        String respCode = head.getString("respCode");
//        if ("000000".equals(respCode)) {
//
//            JSONObject body = jsonObject.getJSONObject("body");
//
//            String orderStatus = body.getString("orderStatus");
//            if ("1".equals(orderStatus)) {
//                System.out.println(orderStatus);
//            }
//
//        }

//         ,sign: signType:SHA1WithRSA
//        String ss = "{\"amount\":1.00,\"feeAmt\":0,\"mid\":\"6888800118807\",\"orderNo\":\"202302071057356\",\"orderStatus\":\"00\",\"payeeInfo\":{\"payeeAccName\":\"孙珂\",\"payeeAccNo\":\"200734000070011\",\"payeeMemID\":\"1622537043820867584\"},\"payerInfo\":{\"payerAccName\":\"杨金毅\",\"payerAccNo\":\"200734000070006\",\"payerMemID\":\"1621455846046859264\"},\"respCode\":\"00000\",\"respMsg\":\"成功\",\"respTime\":\"20230207105822\",\"sandSerialNo\":\"CEAS23020710360624100000098013\",\"transType\":\"C2C_TRANSFER\",\"userFeeAmt\":0.05}";
//        String sign = "qTzUV6MQupeTqHiZ6DBqjw5NEFbE4cDoJTrpHze2sqPUe3n3QgmXL/xBGjYM7Esot49Dx+Xb5PUk3PdM7b0a5XW012chiGMmJBtZ8vOdt9+2hBuIsDts9TYuoPZHIR2LTrKj2xcgsW5PT4y9TrrqlwrUjzewJQYkU2LjKUmoH05vC/BBP7eEPdjw7an2GGgboUd3T8uSbq8df9lyiPagvaFH2ebVeaLuLgPZiPX5JjPPiu0h71TRDedHA0xpdTAm3YSmeTtaX7c3mcqXA/uMwqdq10uJmK6Iq5wtgQhAHFPWkkCoEqhrRdWy/JBeSK+lOWhB2BHT05lTmJGfj+Jd0w==";
//        CertUtil.init("/Users/sunke/dev/certs/flash/sand_pro.cer");
//
//        JSONObject jsonObject = JSON.parseObject(ss);
//        String respCode = jsonObject.getString("respCode");
//        String orderStatus = jsonObject.getString("orderStatus");
//        if ("00000".equals(respCode) && "00".equals(orderStatus)) {
//            Long orderNo = jsonObject.getLong("orderNo");
//            System.out.println(orderNo);
//        }
//        boolean verify = CryptoUtil.verifyDigitalSign(ss.getBytes("UTF-8"), Base64.decodeBase64(sign), CertUtil.getPublicKey(), "SHA1WithRSA");
//        System.out.println(verify);
//
    }
}
