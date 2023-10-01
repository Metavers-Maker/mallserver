package com.muling.mall.ums.listener;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.muling.common.constant.GlobalConstants;
import com.muling.common.constant.RedisConstants;
import com.muling.mall.ums.enums.AuthStatusEnum;
import com.muling.mall.ums.enums.MemberAuthStatusEnum;
import com.muling.mall.ums.event.MemberAuthSuccessEvent;
import com.muling.mall.ums.pojo.entity.UmsMember;
import com.muling.mall.ums.pojo.entity.UmsMemberAuth;
import com.muling.mall.ums.service.IAuthService;
import com.muling.mall.ums.service.IUmsMemberAuthService;
import com.muling.mall.ums.service.IUmsMemberService;
import com.muling.mall.ums.util.ResponseAuth;
import com.muling.mall.ums.util.ResponseCodeEnum;
import com.muling.mall.ums.util.ResponseDTO;
import com.muling.mall.ums.util.ResultEnum;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class MemberAuthListener {

    @Resource
    private IUmsMemberAuthService memberAuthService;

    @Resource
    private IUmsMemberService memberService;

    @Resource
    private IAuthService authService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(value = GlobalConstants.MQ_MEMBER_AUTH_QUEUE, durable = "true"),
                    exchange = @Exchange(value = GlobalConstants.MQ_MEMBER_AUTH_EXCHANGE),
                    key = GlobalConstants.MQ_MEMBER_AUTH_KEY
            )
    })
    @RabbitHandler
    public void process(String authInfo, Message message, Channel channel) throws Exception {
        log.info("接收到消息：{}", authInfo);
        UmsMemberAuth memberAuth = JSONUtil.toBean(authInfo, UmsMemberAuth.class);
        UmsMember umsMember = memberService.getById(memberAuth.getMemberId());
        // 调用认证服务
        ResponseAuth responseAuth = authService.auth(memberAuth.getRealName(), memberAuth.getIdCard(), memberAuth.getMobile());
        String response = JSONUtil.toJsonStr(responseAuth);
        if (responseAuth.getError_code() == 0) {
            String ret = responseAuth.getResult().getVerificationResult();
            if (StringUtils.isNotBlank(ret) && StringUtils.equals(ret, "1") == true) {
                // 认证通过
                memberAuth.setStatus(AuthStatusEnum.PASS);
                umsMember.setAuthStatus(MemberAuthStatusEnum.AUTHED.getValue());
            } else {
                // 认证失败
                memberAuth.setStatus(AuthStatusEnum.FAIL);
                umsMember.setAuthStatus(MemberAuthStatusEnum.RE_AUTH.getValue());
                log.error("调用认证服务失败：{} {}", authInfo, response);
            }
        } else {
            // 认证失败
            memberAuth.setStatus(AuthStatusEnum.FAIL);
            umsMember.setAuthStatus(MemberAuthStatusEnum.RE_AUTH.getValue());
            log.error("调用认证服务失败：{} {}", authInfo, response);
        }
        //
        memberAuthService.updateAuth(umsMember, memberAuth, authInfo, response, responseAuth);
        if (memberAuth.getStatus() == AuthStatusEnum.PASS) {
            stringRedisTemplate.opsForValue().set(RedisConstants.UMS_AUTH_SUFFIX + memberAuth.getMemberId(), "1");
            MemberAuthSuccessEvent event = new MemberAuthSuccessEvent().setMember(umsMember);
            rabbitTemplate.convertAndSend(GlobalConstants.MQ_MEMBER_AUTH_SUCCESS_QUEUE, JSONUtil.toJsonStr(event));
        }
        log.info("实名认证返回结果：{} {} ", authInfo, response);
    }
}
