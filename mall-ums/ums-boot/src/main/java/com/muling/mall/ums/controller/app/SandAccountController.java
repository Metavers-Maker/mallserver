package com.muling.mall.ums.controller.app;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.muling.common.cert.util.sand.CertUtil;
import com.muling.common.cert.util.sand.SignatureUtils;
import com.muling.common.result.Result;
import com.muling.common.web.annotation.RequestLimit;
import com.muling.common.web.util.IPUtils;
import com.muling.mall.ums.pojo.dto.MemberSandDTO;
import com.muling.mall.ums.pojo.form.BindWxopenForm;
import com.muling.mall.ums.pojo.form.RegisterForm;
import com.muling.mall.ums.pojo.form.ResetPasswordForm;
import com.muling.mall.ums.pojo.form.ResetTradePasswordForm;
import com.muling.mall.ums.service.ISandAccountService;
import com.muling.mall.ums.service.IUmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Api(tags = "app-杉德账户")
@RestController
@RequestMapping("/app-api/v1/sand/account")
@Slf4j
@RequiredArgsConstructor
public class SandAccountController {

    private final ISandAccountService sandAccountService;

    @ApiOperation(value = "开通杉德封装版账户")
    @PostMapping("/create")
    @RequestLimit(count = 5, waits = 1, limitFiledType = RequestLimit.LimitFiledType.IP)
    public Result create(HttpServletRequest req) {
        String ipAddr = IPUtils.getIpAddr(req);
        String url = sandAccountService.createSandAccount(ipAddr, null);
        return Result.success(url);
    }

    /**
     * sandNotify结果回调
     *
     * @param req  请求
     * @param resp 响应
     */
    @ApiOperation(value = "杉德封装版账户回调")
    @RequestMapping("/callback")
    public String callBack(HttpServletRequest req, HttpServletResponse resp) {
        log.info("sand回调");
        try {
            Map<String, String[]> parameterMap = req.getParameterMap();
            if (parameterMap != null && !parameterMap.isEmpty()) {
                String data = req.getParameter("data");
                String sign = req.getParameter("sign");
                String signType = req.getParameter("signType");
                // 验证签名
                log.info("sand回调结果 data:{} ,sign:{} signType:{}", data, sign, signType);
                com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
                jsonObject.put("data", data);
                jsonObject.put("sign", sign);
                jsonObject.put("signType", signType);
                boolean verify = SignatureUtils.verify(data, sign, signType, CertUtil.getPublicKey());
                log.info("sand回调验签：{}", verify);
                if (verify) {
                    JSONObject object = JSON.parseObject(data);
                    String bizType = object.getString("bizType");
                    String respCode = object.getString("respCode");
                    if ("SIGN_PROTOCOL".equals(bizType) && "00000".equals(respCode)) {
//                        Long bizUserNo = object.getLong("bizUserNo");
//                        String masterAccount = object.getString("masterAccount");
//                        MemberSandDTO memberSandDTO = new MemberSandDTO();
//                        memberSandDTO.setMemberId(bizUserNo);
//                        memberSandDTO.setSandId(masterAccount);
//                        memberFeignClient.addSandAccount(memberSandDTO);
                    }
                }

            }
        } catch (Exception e) {
            log.info("sand回调异常", e);
        }
        return "respCode=000000";
    }

    //
}
