package com.muling.common.sms.service;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.muling.common.base.IBaseEnum;
import com.muling.common.enums.VCodeTypeEnum;
import com.muling.common.sms.config.SmsConfig;
import com.muling.common.sms.enums.SmsTypeEnum;
import com.muling.common.sms.response.AllNetResponse;
import com.muling.common.sms.response.YunXinResponse;
import com.muling.common.sms.utils.CheckSumBuilder;
import com.muling.common.util.VCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//import com.muling.common.web.service.ValidateService;

/**
 * @author <a href="mailto:xianrui0365@163.com">haoxianrui</a>
 * @date 2021/10/13 23:04
 */
@Service
@Slf4j
public class SmsService {

    @Qualifier("smsAcsClient")
    @Autowired
    private IAcsClient client;

    @Autowired
    private SmsConfig smsConfig;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 发送短信
     *
     * @param phoneNumber 手机号
     * @return
     */
    public boolean sendSmsCode(Integer type, String phoneNumber) {
        //防止同一个用户提交多个订单并未处理，需要检查redis中是否存在未处理完成的订单
        RLock lock = redissonClient.getLock("sms:lock:" + phoneNumber);
        try {
            lock.lock();
            String sCode = VCodeUtils.getCode(stringRedisTemplate, IBaseEnum.getEnumByValue(type, VCodeTypeEnum.class), phoneNumber);
            if (StrUtil.isNotBlank(sCode)) {
                return true;
            }
            // 随机生成4位的验证码 并保存到Redis中
            String code = RandomUtil.randomNumbers(4);
            VCodeUtils.setCode(stringRedisTemplate, IBaseEnum.getEnumByValue(type, VCodeTypeEnum.class), phoneNumber, code);
            SmsTypeEnum smsType = IBaseEnum.getEnumByValue(smsConfig.getType(), SmsTypeEnum.class);
            if (smsType == SmsTypeEnum.ALI_YUN) {
                return sendSmsByAliYun(type, phoneNumber, code);
            } else if (smsType == SmsTypeEnum.ALL_NET) {
                return sendSmsByAllNet(type, phoneNumber, code);
            } else if (smsType == SmsTypeEnum.YUN_XIN) {
                return sendSmsByYunXin(type, phoneNumber, code);
            }
        } catch (Exception e) {
            VCodeUtils.delete(stringRedisTemplate, IBaseEnum.getEnumByValue(type, VCodeTypeEnum.class), phoneNumber);
            log.error("短信异常：", e);
            return false;
        } finally {
            //释放锁
            if (lock.isLocked()) {
                lock.unlock();
            }
        }
        return true;
    }

    public boolean sendSmsByAliYun(Integer type, String phoneNumber, String code) throws Exception {

        // 创建通用的请求对象
        CommonRequest request = new CommonRequest();
        // 指定请求方式
        request.setSysMethod(MethodType.POST);
        // 短信api的请求地址  固定
        request.setSysDomain(smsConfig.getAliYun().getDomain());
        // 签名算法版本  固定
        request.setSysVersion("2017-05-25");
        // 请求 API 的名称
        request.setSysAction("SendSms");
        // 指定地域名称
        request.putQueryParameter("RegionId", smsConfig.getAliYun().getRegionId());
        // 要给哪个手机号发送短信  指定手机号
        request.putQueryParameter("PhoneNumbers", phoneNumber);
        // 您的申请签名
        request.putQueryParameter("SignName", smsConfig.getAliYun().getSignName());
        // 您申请的模板 code
        if (type == VCodeTypeEnum.REGISTER.getValue()) {
            //注册模版
            request.putQueryParameter("TemplateCode", smsConfig.getAliYun().getTemplateCode());
        } else if (type == VCodeTypeEnum.LOGIN.getValue()) {
            //登录模版
            request.putQueryParameter("TemplateCode", smsConfig.getAliYun().getTemplateCode());
        } else if (type == VCodeTypeEnum.RESET_PASSWORD.getValue()) {
            //重置密码模版
            request.putQueryParameter("TemplateCode", smsConfig.getAliYun().getTemplateCode());
        } else if (type == VCodeTypeEnum.RESET_TRADE_PASSWORD.getValue()) {
            //重置交易密码模版
            request.putQueryParameter("TemplateCode", "SMS_461060190");
        } else if (type == VCodeTypeEnum.BIND_THIRD_PLATFORM.getValue()) {
            //绑定三方平台
            request.putQueryParameter("TemplateCode", "SMS_460925231");
        } else if (type == VCodeTypeEnum.MARKET_SELL.getValue()) {
            //寄售验证码
            request.putQueryParameter("TemplateCode", smsConfig.getAliYun().getTemplateCode());
        } else if (type == VCodeTypeEnum.UN_REGISTER.getValue()) {
            //注销验证码
            request.putQueryParameter("TemplateCode", smsConfig.getAliYun().getTemplateCode());
        } else {
            request.putQueryParameter("TemplateCode", smsConfig.getAliYun().getTemplateCode());
        }
        // 这里的key就是短信模板中的 ${xxxx}
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        request.putQueryParameter("TemplateParam", JSONUtil.toJsonStr(params));
        try {
            CommonResponse response = client.getCommonResponse(request);
            return response.getHttpResponse().isSuccess();
        } catch (ServerException e) {
            throw e;
        } catch (ClientException e) {
            throw e;
        }
    }

    public boolean sendSmsByAllNet(Integer type, String phoneNumber, String code) throws Exception {

        String content = "【元物之门】验证码：" + code + "（有效期为5分钟），为保证账户安全，请勿将验证码提供给他人，若非本人操作请忽略。";
        String ts = String.valueOf(System.currentTimeMillis());
        String md5 = SecureUtil.md5(smsConfig.getAllNet().getUserId() + ts + smsConfig.getAllNet().getApiKey());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.add("userid", smsConfig.getAllNet().getUserId());
        params.add("ts", ts);
        params.add("sign", md5.toLowerCase());
        params.add("mobile", phoneNumber);
        params.add("msgcontent", content);
        params.add("extnum", "");
        try {
            ResponseEntity<AllNetResponse> result = restTemplate.postForEntity(smsConfig.getAllNet().getUrl(), params, AllNetResponse.class);
            log.info("发送短信结果：{}.{}.{}", phoneNumber, code, JSONUtil.toJsonStr(result));
            AllNetResponse body = result.getBody();
            return "0".equals(body.getCode());
        } catch (Exception e) {
            log.error("", e);
            throw e;
        }
    }

    public boolean sendSmsByYunXin(Integer type, String phoneNumber, String code) throws Exception {
        try {
            String curTime = String.valueOf((new Date()).getTime() / 1000L);
            String nonce = RandomUtil.randomNumbers(4); // 随机生成4位的验证码
            /*
             * 参考计算CheckSum的java代码，在上述文档的参数列表中，有CheckSum的计算文档示例
             */
            String checkSum = CheckSumBuilder.getCheckSum(smsConfig.getYunXin().getAppSecret(), nonce, curTime);

            String templateId = "";
            if (type == VCodeTypeEnum.REGISTER.getValue()) {
                templateId = "19522853";
            } else if (type == VCodeTypeEnum.LOGIN.getValue()) {
                templateId = "19522058";
            } else if (type == VCodeTypeEnum.RESET_PASSWORD.getValue()) {
                templateId = "19522057";
            } else if (type == VCodeTypeEnum.RESET_TRADE_PASSWORD.getValue()) {
                templateId = "19522057";
            }

            MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
            map.add("templateid", templateId);
            map.add("mobile", phoneNumber);
            map.add("authCode", code);

            HttpHeaders headers = new HttpHeaders();
            MediaType contentType = MediaType.parseMediaType("application/x-www-form-urlencoded;charset=utf-8");
            headers.setContentType(contentType);
            headers.add("AppKey", smsConfig.getYunXin().getAppKey());
            headers.add("Nonce", nonce);
            headers.add("CurTime", curTime);
            headers.add("CheckSum", checkSum);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
            ResponseEntity<YunXinResponse> result = restTemplate.postForEntity(smsConfig.getYunXin().getUrl(), request, YunXinResponse.class);
            log.info("发送短信结果：{}.{}.{}", phoneNumber, code, JSONUtil.toJsonStr(result));
            YunXinResponse body = result.getBody();
            return "200".equals(body.getCode());
        } catch (Exception e) {
            log.error("发送短信异常", e);
            throw e;
        }
    }

}
