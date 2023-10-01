package com.muling.mall.ums.controller.admin;

import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.annotation.AutoLog;
import com.muling.common.constant.GlobalConstants;
import com.muling.common.constant.RedisConstants;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.ums.constant.UmsConstants;
import com.muling.mall.ums.enums.AuthStatusEnum;
import com.muling.mall.ums.enums.MemberAuthStatusEnum;
import com.muling.mall.ums.event.MemberAuthSuccessEvent;
import com.muling.mall.ums.pojo.entity.UmsMember;
import com.muling.mall.ums.pojo.entity.UmsMemberAuth;
import com.muling.mall.ums.pojo.entity.UmsMemberAuthLog;
import com.muling.mall.ums.pojo.form.MemberAuthCreateForm;
import com.muling.mall.ums.service.IUmsMemberAuthLogService;
import com.muling.mall.ums.service.IUmsMemberAuthService;
import com.muling.mall.ums.service.IUmsMemberService;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Api(tags = "admin-会员认证")
@RestController("MemberAuthController")
@RequestMapping("/api/v1/members/auth")
@Slf4j
@AllArgsConstructor
public class MemberAuthController {

    private IUmsMemberAuthService umsMemberAuthService;

    private IUmsMemberAuthLogService umsMemberAuthLogService;

    private IUmsMemberService umsMemberService;

    private StringRedisTemplate stringRedisTemplate;

    private RabbitTemplate rabbitTemplate;

    private RedissonClient redissonClient;

    @ApiOperation(value = "分页列表")
    @GetMapping
    public PageResult<UmsMemberAuth> listDevicesWithPage(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "会员ID") Long memberId,
            @ApiParam(value = "手机号") String mobile,
            @ApiParam(value = "真实姓名") String realName,
            @ApiParam(value = "身份证") String idCard,
            @ApiParam(value = "开始时间") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime started,
            @ApiParam(value = "结束时间") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime ended

    ) {
        LambdaQueryWrapper<UmsMemberAuth> wrapper = Wrappers.<UmsMemberAuth>lambdaQuery();
        wrapper.eq(memberId != null, UmsMemberAuth::getMemberId, memberId);
        wrapper.eq(StrUtil.isNotBlank(mobile), UmsMemberAuth::getMobile, mobile);
        wrapper.like(StrUtil.isNotBlank(realName), UmsMemberAuth::getRealName, realName);
        wrapper.eq(StrUtil.isNotBlank(idCard), UmsMemberAuth::getIdCard, idCard);
        wrapper.ge(started != null, UmsMemberAuth::getCreated, started);
        wrapper.le(ended != null, UmsMemberAuth::getCreated, ended);
        wrapper.orderByDesc(UmsMemberAuth::getIdCard);
        IPage<UmsMemberAuth> result = umsMemberAuthService.page(new Page<UmsMemberAuth>(pageNum, pageSize), wrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "日志分页列表")
    @GetMapping("logs")
    public PageResult<UmsMemberAuthLog> logs(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "会员ID") Long memberId
    ) {
        LambdaQueryWrapper<UmsMemberAuthLog> wrapper = Wrappers.<UmsMemberAuthLog>lambdaQuery();
        wrapper.eq(memberId != null, UmsMemberAuthLog::getMemberId, memberId);
        wrapper.orderByDesc(UmsMemberAuthLog::getCreated);
        IPage<UmsMemberAuthLog> result = umsMemberAuthLogService.page(new Page<UmsMemberAuthLog>(pageNum, pageSize), wrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "创建认证信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberAuthCreateForm", value = "memberAuthCreateForm", required = true, paramType = "body", dataType = "MemberAuthCreateForm", dataTypeClass = MemberAuthCreateForm.class)
    })
    @PostMapping("/create/{memberId}")
    public Result create(
            @PathVariable Long memberId,
            @Valid @RequestBody MemberAuthCreateForm memberAuthCreateForm) throws Exception {
        // 判断身份证号是否合法
        if (!IdcardUtil.isValidCard(memberAuthCreateForm.getIdCard())) {
            return Result.failed(ResultCode.PARAM_ERROR, "身份证号不合法");
        }
        boolean chinese = isChineseName(memberAuthCreateForm.getRealName());
        if (!chinese) {
            return Result.failed(ResultCode.PARAM_ERROR, "姓名必须为中文");
        }
        boolean result = umsMemberAuthService.create(memberId, memberAuthCreateForm);
        return Result.success(result);
    }

    @ApiOperation(value = "设置认证成功")
    @PutMapping("/{memberId}")
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    public Result setAuthSuccess(@PathVariable Long memberId) {

        RLock lock = redissonClient.getLock(UmsConstants.USER_LOCK_AUTH_PREFIX + memberId);
        try {
            lock.lock();
            UmsMember umsMember = umsMemberService.getById(memberId);
            if (umsMember.getAuthStatus() == MemberAuthStatusEnum.AUTHED.getValue()) {
                return Result.judge(false);
            }
            // 认证通过
            umsMember.setAuthStatus(MemberAuthStatusEnum.AUTHED.getValue());

            UmsMemberAuth umsMemberAuth = umsMemberAuthService.getOne(Wrappers.<UmsMemberAuth>lambdaQuery().eq(UmsMemberAuth::getMemberId, memberId));
            umsMemberAuth.setStatus(AuthStatusEnum.PASS);

            umsMemberService.updateById(umsMember);
            umsMemberAuthService.updateById(umsMemberAuth);

            stringRedisTemplate.opsForValue().set(RedisConstants.UMS_AUTH_SUFFIX + memberId, "1");
            MemberAuthSuccessEvent event = new MemberAuthSuccessEvent().setMember(umsMember);
            rabbitTemplate.convertAndSend(GlobalConstants.MQ_MEMBER_AUTH_SUCCESS_QUEUE, JSONUtil.toJsonStr(event));
        } finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return Result.judge(true);
    }

    /**
     * 中文包括少数民族·字符
     *
     * @param name
     * @return
     */
    public boolean isChineseName(String name) {
        return ReUtil.isMatch("^((?![\\u3000-\\u303F])[\\u2E80-\\uFE4F]|\\·)*(?![\\u3000-\\u303F])[\\u2E80-\\uFE4F](\\·)*$", name);
    }

}
