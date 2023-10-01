package com.muling.mall.farm.controller.app;

import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.muling.common.result.Result;
import com.muling.mall.farm.converter.FarmDuomobItemConverter;
import com.muling.mall.farm.pojo.dto.FarmAdItemDTO;
import com.muling.mall.farm.pojo.dto.FarmDuomobItemDTO;
import com.muling.mall.farm.pojo.entity.FarmDuomobItem;
import com.muling.mall.farm.pojo.form.admin.FarmConfigForm;
import com.muling.mall.farm.pojo.form.admin.FarmDuomobForm;
import com.muling.mall.farm.service.IFarmAdItemService;
import com.muling.mall.farm.service.IFarmAdService;
import com.muling.mall.farm.service.IFarmDuomobItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 广告回调接口
 */
@Api(tags = "app-广告回调接口")
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/callback-api/v1/adv")
public class AdvCallbackController {

    private final IFarmAdService farmAdService;
    private static final String SecretKey = "7c068827dd21d01c6a361332d2992ec1";//这里需要替换
    //
    private final IFarmDuomobItemService farmDuomobItemService;
    private static final String SecretKeyDuomob = "53d650868d7f02180edb01ecbccde6f5";

    /**
     * 广告激励结果回调
     *
     * @param request
     * @return
     */
    @GetMapping(value = "/notify", produces = MediaType.TEXT_PLAIN_VALUE)
    public String adNotify(HttpServletRequest request) {
        log.info("FarmAd返回通知");
        JSONObject obj = JSONUtil.createObj();
        try {
            String user_id = URLUtil.decode(request.getParameter("user_id"));//用户 ID—需要在前端 SDK 回传
            String custom_parameters = URLUtil.decode(request.getParameter("custom_parameters"));//用户自定义数据—需要在前端 SDK 回 传
            String app_id = URLUtil.decode(request.getParameter("app_id"));//平台提供的应用 ID
            String place_id = URLUtil.decode(request.getParameter("place_id"));//平台提供的广告位 ID
            String transaction_id = URLUtil.decode(request.getParameter("transaction_id"));//平台生成的唯一交易 ID
            String reward_name = URLUtil.decode(request.getParameter("reward_name"));//奖励名称
            String reward_count = URLUtil.decode(request.getParameter("reward_count"));//奖励数量
            String sign = URLUtil.decode(request.getParameter("sign"));//签名信息
            String reward_ecpm = URLUtil.decode(request.getParameter("reward_ecpm"));//奖励 ecpm 价格
            //验签:SecretKey  需要替换成正式的
            //isValid  code message的值需要问对方成功失败具体是啥
            String decrypt = DigestUtil.sha256Hex(SecretKey + ":" + transaction_id);
            log.info("验签:sign{},decode_sign:{}",sign,decrypt);
            if (!decrypt.equals(sign)) {
                obj.set("isValid", "false");
                obj.set("code", "500");
                obj.set("message", "验签失败");
                return obj.toString();
            }
            //
            JSONObject customParameters = JSONUtil.parseObj(custom_parameters);
            if (customParameters.get("adSn")!=null) {
                Long adSn = Long.valueOf(customParameters.get("adSn").toString());
                FarmAdItemDTO farmAdItemDTO = new FarmAdItemDTO();
                farmAdItemDTO.setMemberId(Long.valueOf(user_id));
                farmAdItemDTO.setAdSn(adSn);
                farmAdItemDTO.setAdType(Integer.valueOf(customParameters.get("adType").toString()));
                farmAdItemDTO.setTransId(transaction_id);
                farmAdItemDTO.setAdId(place_id);
                farmAdItemDTO.setRewardName(reward_name);
                farmAdItemDTO.setRewardCount(Integer.valueOf(reward_count));
                farmAdItemDTO.setEcpm(new BigDecimal(reward_ecpm));
                farmAdService.adCallback(farmAdItemDTO);
            }
            //
        } catch (Exception e) {
            obj.set("isValid", "false");
            obj.set("code", "500");
            obj.set("message", "验签失败");
            return obj.toString();
        }
        //成功
        obj.set("isValid", "true");
        obj.set("code", "200");
        obj.set("message", "验签成功");
        return obj.toString();
    }

    public static String generateSignatureDuoMob(final Map<String, String> data, String key) throws Exception {
        Set<String> keySet = data.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            if (k.equals("sign")) {
                continue;
            }
            // 这里需要对value进行encode，如果value已经encode了忽略此步骤
            String value = data.get(k);
            String encodeValue = URLUtil.encodeAll(value);
            sb.append(k).append("=").append(encodeValue).append("&");
        }
        sb.append("key=").append(key);
        log.info("Duomob验签前参数:param:{}",sb.toString());
        return DigestUtils.md5DigestAsHex(sb.toString().getBytes());
    }

    /**
     * Duomob广告激励结果回调
     * @param request
     * @return
     */
    @GetMapping(value = "/duomob", produces = MediaType.TEXT_PLAIN_VALUE)
    public String duomobNotify(HttpServletRequest request) {
        log.info("开始处理激励结果通知-duomob");
        JSONObject obj = JSONUtil.createObj();
        try {
            String user_id = URLUtil.decode(request.getParameter("user_id"));
            String order_id = URLUtil.decode(request.getParameter("order_id"));
            String advert_id = URLUtil.decode(request.getParameter("advert_id"));
            String advert_name = URLUtil.decode(request.getParameter("advert_name"));
            String created = URLUtil.decode(request.getParameter("created"));
            String media_income = URLUtil.decode(request.getParameter("media_income"));
            String member_income = URLUtil.decode(request.getParameter("member_income"));
            String media_id = URLUtil.decode(request.getParameter("media_id"));
            String device_id = URLUtil.decode(request.getParameter("device_id"));
            String content = URLUtil.decode(request.getParameter("content"));
            String extra = URLUtil.decode(request.getParameter("extra"));//扩展信息
            String sign = URLUtil.decode(request.getParameter("sign"));//签名信息

            //
            log.info("user_id:{},order_id:{},advert_id:{},advert_name:{},created:{},media_income:{},member_income:{},media_id:{},device_id:{},content:{},sign:{}",
                    user_id, order_id, advert_id, advert_name, created, media_income, member_income, media_id, device_id,content,sign);
            //
            //验签:SecretKey  需要替换成正式的
            Map<String, String> signMap = new HashMap<String, String>();
            signMap.put("order_id", order_id);
            signMap.put("advert_id", advert_id);
            signMap.put("advert_name", advert_name);
            signMap.put("created", created);
            signMap.put("media_income", media_income);
            signMap.put("member_income", member_income);
            signMap.put("media_id", media_id);
            signMap.put("user_id", user_id);
            signMap.put("device_id", device_id);
            signMap.put("content", content);
            String localSign = generateSignatureDuoMob(signMap, SecretKeyDuomob);
            log.info("Duomob验签:sign{},decode_sign:{}",sign,localSign);
            //
            if (!localSign.equals(sign)) {
                obj.set("status_code", "403");
                obj.set("message", "验签失败");
                return obj.toString();
            }
            //
            log.info("验签通过，保存数据");
            //
            FarmDuomobItem farmDuomobItem = new FarmDuomobItem();
            farmDuomobItem.setMemberId(Long.valueOf(user_id));
            farmDuomobItem.setOrderId(order_id);
            farmDuomobItem.setAdvertName(advert_name);
            farmDuomobItem.setAdvertId(Integer.valueOf(advert_id));
            farmDuomobItem.setMediaId(media_id);
            farmDuomobItem.setMediaIncome(new BigDecimal(media_income));
            farmDuomobItem.setMemberIncome(new BigDecimal(member_income));
            farmDuomobItem.setDeviceId(device_id);
            farmDuomobItem.setContent(content);
            farmDuomobItem.setGenTime(Integer.valueOf(created));
            farmDuomobItem.setExtra(JSONUtil.parseObj(extra));
            log.info("Duomob-Save: pre-save{}",farmDuomobItem.toString());
            boolean f = farmDuomobItemService.saveDTO(farmDuomobItem);
            log.info("Duomob-Save:after-save{}",String.valueOf(f));
        } catch (Exception e) {
            obj.set("status_code", "403");
            obj.set("message", "验签失败");
            return obj.toString();
        }
        //成功
        obj.set("status_code", "200");
        obj.set("message", "验签成功");
        return obj.toString();
    }

}
