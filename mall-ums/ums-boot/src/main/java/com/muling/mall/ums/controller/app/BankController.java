package com.muling.mall.ums.controller.app;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.muling.common.result.Result;
import com.muling.common.web.annotation.RequestLimit;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.ums.enums.AccountChainEnum;
import com.muling.mall.ums.pojo.dto.MemberAccountChainDTO;
import com.muling.mall.ums.pojo.form.*;
import com.muling.mall.ums.pojo.vo.BankBindVO;
import com.muling.mall.ums.pojo.vo.BankVO;
import com.muling.mall.ums.service.IUmsAccountChainService;
import com.muling.mall.ums.service.IUmsBankService;
import com.muling.mall.ums.service.IUmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Api(tags = "app-银行卡账户")
@RestController
@RequestMapping("/app-api/v1/bank/account")
@Slf4j
@RequiredArgsConstructor
public class BankController {

    private final IUmsBankService bankService;

    @ApiOperation(value = "获取用户银行卡列表")
    @GetMapping("/list")
    public Result<List<BankVO>> list(
            @ApiParam(value = "支付平台类型", example = "0") Integer platType
    ) {
        List<BankVO> result = bankService.listBank(platType);
        return Result.success(result);
    }

    @ApiOperation(value = "申请绑定银行卡")
    @PostMapping("/bind/apply")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.CUSTOM_VALUE, field = "mobile")
    public Result bindApply(@Validated @RequestBody BankBindForm bindForm) {
        Long memberId = MemberUtils.getMemberId();
        BankBindVO bindVO = bankService.bindCard(bindForm, memberId);
        return Result.success(bindVO);
    }

    @ApiOperation(value = "确认绑定银行卡")
    @PostMapping("/bind/ensure")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.CUSTOM_VALUE, field = "mobile")
    public Result bindEnsure(@Validated @RequestBody BankBindEnsureForm bindEnsureForm) {
        Long memberId = MemberUtils.getMemberId();
        boolean ret = bankService.bindCardEnsure(bindEnsureForm, memberId);
        return Result.judge(ret);
    }

    @ApiOperation(value = "解绑银行卡")
    @PostMapping("/unbind/{id}")
//    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.CUSTOM_VALUE, field = "mobile")
    public Result unbind(@Validated @PathVariable Long id) {
        Long memberId = MemberUtils.getMemberId();
        boolean result = bankService.unbindCard(id, memberId);
        return Result.judge(result);
    }

    @ApiOperation(value = "设置默认银行卡")
    @PostMapping("/userd/{id}")
//    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.CUSTOM_VALUE, field = "mobile")
    public Result usedBank(@Validated @PathVariable Long id) {
        Long memberId = MemberUtils.getMemberId();
        boolean result = bankService.usedBank(id, memberId);
        return Result.judge(result);
    }


    /**
     * sand 卡片回调
     * */
    @RequestMapping("/sand/callback")
    public String sandCallBack(HttpServletRequest req, HttpServletResponse resp) {
        try {
            Map<String, String[]> parameterMap = req.getParameterMap();
            if (parameterMap != null && !parameterMap.isEmpty()) {
                String data = req.getParameter("data");
                String sign = req.getParameter("sign");
                String signType = req.getParameter("signType");
                // 验证签名
                log.info("sand_card 回调结果 data:{} ,sign:{} signType:{}", data, sign, signType);
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("data", data);
//                jsonObject.put("sign", sign);
//                jsonObject.put("signType", signType);
//                boolean verify = SignatureUtils.verify(data, sign, signType, CertCache.getCertCache().getPublicKey());
//                log.info("sand_pay回调验签：{}", verify);
//                if (verify) {
//                    JSONObject jsonData = JSON.parseObject(data);
//                    String respCode = jsonData.getString("respCode");
//                    String orderStatus = jsonData.getString("orderStatus");
//                    if ("00000".equals(respCode) && "00".equals(orderStatus)) {
//                        String orderNo = jsonData.getString("orderNo");
//                        OmsOrder nftOrder = orderService.getById(orderNo);
//                        if (nftOrder.getStatus() == OrderStatusEnum.PENDING_PAYMENT.getValue()) {
//                            log.info("支付成功，订单状态正常：{}", JSON.toJSONString(nftOrder));
//                            orderService.payOrderSuccess(nftOrder);
//                            log.info("支付成功paySuccess：{},订单:{}", paySuccess, JSON.toJSONString(nftOrder));
//                            if (paySuccess) {
//                                redisTemplate.delete("waitPaymentUserOrder:" + nftOrder.getUid());
//                                String msg = JSON.toJSONString(nftOrder);
//                                provider.sendDelayMsg(RabbitQueueConfiguration.Exchange_PAY_RESULT,
//                                        RabbitQueueConfiguration.Exchange_KEY_PAY_RESULT, msg, 0);
//                                log.info("发送上链消息：{}", msg);
//                            }
//                        } else {
//                            log.info("支付成功，被关闭回调结果订单已：{}", JSON.toJSONString(nftOrder));
//                        }
//                    }
//                }
            }
        } catch (Exception e) {
            log.info("sand回调异常", e);
        }
        return "respCode=000000";
    }


}
