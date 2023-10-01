package com.muling.mall.ums.controller.admin;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.muling.common.base.IBaseEnum;
import com.muling.common.constant.GlobalConstants;
import com.muling.common.enums.VCodeTypeEnum;
import com.muling.common.exception.BizException;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.common.util.VCodeUtils;
import com.muling.mall.ums.event.MemberAuthSuccessEvent;
import com.muling.mall.ums.pojo.entity.UmsMember;
import com.muling.mall.ums.service.IUmsMemberService;
import com.muling.mall.ums.test.Message;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@Api(tags = "admin-测试相关")
@RestController("TestController")
@RequestMapping("/api/v1/test")
@Slf4j
@AllArgsConstructor
public class TestController {

    private final StringRedisTemplate stringRedisTemplate;

    private final IUmsMemberService memberService;

    private final RabbitTemplate rabbitTemplate;


    @ApiOperation(value = "[测试使用]-发送验证码")
    @PostMapping("/send-v-code")
    public Result sendVCode(
            @ApiParam("验证码类型") @RequestParam Integer type,
            @ApiParam("手机号") @RequestParam String mobile) {

        VCodeTypeEnum value = IBaseEnum.getEnumByValue(type, VCodeTypeEnum.class);
        if (value == null) {
            throw new BizException(ResultCode.PARAM_ERROR, "验证码类型不匹配");
        }
        String code = RandomUtil.randomNumbers(4); // 随机生成4位的验证码

        VCodeUtils.setCode(stringRedisTemplate, IBaseEnum.getEnumByValue(type, VCodeTypeEnum.class), mobile, code);

        return Result.success();
    }

    @ApiOperation(value = "[测试使用]-获得验证码")
    @GetMapping("/v-code")
    public Result getVCode(
            @ApiParam("验证码类型") @RequestParam Integer type,
            @ApiParam("手机号") @RequestParam String mobile) {

        String code = VCodeUtils.getCode(stringRedisTemplate, IBaseEnum.getEnumByValue(type, VCodeTypeEnum.class), mobile);

        return Result.success(code);
    }

    @ApiOperation(value = "[测试使用]-认证成功")
    @GetMapping("/auth-success")
    public Result authSuccess(
            @ApiParam("手机号") @RequestParam String mobile) {

        UmsMember member = memberService.getOne(Wrappers.<UmsMember>lambdaQuery().eq(UmsMember::getMobile, mobile));

        MemberAuthSuccessEvent event = new MemberAuthSuccessEvent().setMember(member);
        rabbitTemplate.convertAndSend(GlobalConstants.MQ_MEMBER_AUTH_SUCCESS_QUEUE, JSONUtil.toJsonStr(event));
        return Result.success();
    }

    @ApiOperation(value = "[测试使用]-分发多模块消息")
    @GetMapping("/dispatch")
    public Result dispatch(
            @ApiParam("手机号") @RequestParam String mobile) {

        Message message = new Message();
        message.setName(mobile);
        rabbitTemplate.convertAndSend("fanout_exchange_order", "", message);
        return Result.success();
    }
}
