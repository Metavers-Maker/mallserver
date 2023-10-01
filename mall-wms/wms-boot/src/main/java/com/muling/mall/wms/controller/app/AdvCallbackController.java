package com.muling.mall.wms.controller.app;

import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
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
 * 广告回调接口
 */
@Api(tags = "app-广告回调接口")
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/callback-api/v1/adv")
public class AdvCallbackController {

    private static final String SecretKey = "7c068827dd21d01c6a361332d2992ec1";//这里需要替换

    /**
     * 广告激励结果回调
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/notify", produces = MediaType.TEXT_PLAIN_VALUE)
    public String googleNotify(HttpServletRequest request) {
        log.info("开始处理Google回调");
        JSONObject obj = JSONUtil.createObj();
        try {
            String ad_network = URLUtil.encode(request.getParameter("ad_network"));//广告联盟的标识符
            String ad_unit = URLUtil.encode(request.getParameter("ad_unit"));// 广告单元 ID
            String custom_data = URLUtil.encode(request.getParameter("custom_data"));//自定义数据字符串
            String key_id = URLUtil.encode(request.getParameter("key_id"));//验证 SSV 回调的密钥
            String reward_amount = URLUtil.encode(request.getParameter("reward_amount"));//奖励数量
            String reward_item = URLUtil.encode(request.getParameter("reward_item"));//奖励物品
            String signature = URLUtil.encode(request.getParameter("signature"));//签名
            String timestamp = URLUtil.encode(request.getParameter("timestamp"));//时间戳
            String transaction_id = URLUtil.encode(request.getParameter("transaction_id"));//交易ID
            String user_id = URLUtil.encode(request.getParameter("user_id"));//用户ID
            log.info("ad_network:{},ad_unit:{},custom_data:{},key_id:{},reward_amount:{},signature:{},timestamp:{},transaction_id:{},user_id:{}",
                    ad_network, ad_unit, custom_data, key_id, reward_amount, reward_item, signature, timestamp, transaction_id,user_id);

//            //解码：现在这样写是为了看对方传的原始数据，测试通过后可以和上面合并成一条代码 例：String user_id = URLUtil.encode(request.getParameter("user_id"));
//            user_id = URLUtil.encode(user_id);//用户 ID—需要在前端 SDK 回传
//            custom_parameters = URLUtil.encode(custom_parameters);//用户自定义数据—需要在前端 SDK 回 传
//            app_id = URLUtil.encode(app_id);//平台提供的应用 ID
//            place_id = URLUtil.encode(place_id);//平台提供的广告位 ID
//            transaction_id = URLUtil.encode(transaction_id);//平台生成的唯一交易 ID
//            reward_name = URLUtil.encode(reward_name);//奖励名称
//            reward_count = URLUtil.encode(reward_count);//奖励数量
//            sign = URLUtil.encode(sign);//签名信息
//            reward_ecpm = URLUtil.encode(reward_ecpm);//奖励 ecpm 价格
//
//
//            //验签:SecretKey  需要替换成正式的
//            //isValid  code message的值需要问对方成功失败具体是啥
//            String decrypt = DigestUtil.sha256Hex(SecretKey + ":" + transaction_id);
//            if (!decrypt.equals(sign)) {
//                obj.set("isValid", "false");
//                obj.set("code", "500");
//                obj.set("message", "验签失败");
//                return obj.toString();
//            }

            //加key为transaction_id的锁执行以下逻辑

            //执行逻辑之前先判断transaction_id是否已经执行过了，防止重复发放奖励


            //具体逻辑


            //执行成功记录transaction_id

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

}
