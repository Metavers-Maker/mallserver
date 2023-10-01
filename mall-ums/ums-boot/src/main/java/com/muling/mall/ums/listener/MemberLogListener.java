package com.muling.mall.ums.listener;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.muling.common.base.IBaseEnum;
import com.muling.common.constant.GlobalConstants;
import com.muling.mall.ums.enums.MemberLogTypeEnum;
import com.muling.mall.ums.pojo.entity.UmsMember;
import com.muling.mall.ums.pojo.entity.UmsMemberLog;
import com.muling.mall.ums.service.IUmsMemberLogService;
import com.muling.mall.ums.service.IUmsMemberService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Component
@Slf4j
public class MemberLogListener {

    @Resource
    private IUmsMemberLogService memberLogService;

    @Resource
    private IUmsMemberService memberService;

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(value = GlobalConstants.MQ_MEMBER_LOG_QUEUE, durable = "true"),
                    exchange = @Exchange(value = GlobalConstants.MQ_MEMBER_LOG_EXCHANGE),
                    key = GlobalConstants.MQ_MEMBER_LOG_KEY
            )
    })
    @RabbitHandler
    public void process(String data, Message message, Channel channel) throws Exception {
        log.debug("接收到消息：{}", data);
        JSONObject jsonObject = JSONUtil.parseObj(data);
        Integer type = jsonObject.getInt("type");
        Long memberId = jsonObject.getLong("member_id");
        String clientId = jsonObject.getStr("client_id");
        String grantType = jsonObject.getStr("grant_type");

        String ip = jsonObject.getStr("ip");

        String deviceId = jsonObject.getStr("device_id");
        String deviceName = jsonObject.getStr("device_name");
        String deviceVersion = jsonObject.getStr("device_version");

        String userAgent = jsonObject.getStr("user-agent");

        memberService.update(new LambdaUpdateWrapper<UmsMember>()
                .eq(UmsMember::getId, memberId)
                .set(UmsMember::getLastLoginType, grantType)
                .set(UmsMember::getLastLoginIp, ip)
                .set(UmsMember::getLastLoginTime, LocalDateTime.now()));

        UmsMemberLog memberLog = new UmsMemberLog()
                .setType(IBaseEnum.getEnumByValue(type, MemberLogTypeEnum.class))
                .setMemberId(memberId)
                .setClientId(clientId)
                .setGrantType(grantType)
                .setDeviceId(deviceId)
                .setIp(ip)
                .setDeviceName(deviceName)
                .setDeviceVersion(deviceVersion)
                .setUserAgent(userAgent);
        memberLogService.save(memberLog);
    }
}
