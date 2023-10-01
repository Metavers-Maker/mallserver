package com.muling.mall.ums.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.mall.ums.pojo.entity.UmsMemberInvite;
import com.muling.mall.ums.pojo.form.MemberInviteForm;
import com.muling.mall.ums.pojo.form.admin.InviteCodeForm;
import com.muling.mall.ums.service.IUmsMemberInviteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Api(tags = "admin-会员邀请")
@RestController("AdminMemberInviteController")
@RequestMapping("/api/v1/members/invite")
@Slf4j
@AllArgsConstructor
public class MemberInviteController {

    private IUmsMemberInviteService memberInviteService;

    @ApiOperation(value = "分页列表")
    @GetMapping("/page")
    public PageResult<UmsMemberInvite> page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "会员ID") Long memberId,
            @ApiParam(value = "邀请者ID") String inviteCode,
            @ApiParam(value = "邀请者ID") Long inviteMemberId,
            @ApiParam(value = "开始时间") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime started,
            @ApiParam(value = "结束时间") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime ended
    ) {
        //inviteNum
        LambdaQueryWrapper<UmsMemberInvite> wrapper = Wrappers.<UmsMemberInvite>lambdaQuery();
        wrapper.eq(memberId != null, UmsMemberInvite::getMemberId, memberId);
        wrapper.eq(inviteCode != null, UmsMemberInvite::getInviteCode, inviteCode);
        wrapper.eq(inviteMemberId != null, UmsMemberInvite::getInviteMemberId, inviteMemberId);
        wrapper.ge(started != null, UmsMemberInvite::getCreated, started);
        wrapper.le(ended != null, UmsMemberInvite::getCreated, ended);
        wrapper.orderByDesc(UmsMemberInvite::getCreated);
        IPage<UmsMemberInvite> result = memberInviteService.page(new Page(pageNum, pageSize), wrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "修改")
    @PutMapping("/{id}")
    public Result update(
            @ApiParam(value = "ID") @PathVariable Long id,
            @RequestBody @Validated MemberInviteForm form
    ) {
        boolean result = memberInviteService.update(id, form);
        return Result.judge(result);
    }

    @ApiOperation(value = "添加邀请码")
    @PutMapping("/invite-code")
    public Result addInviteCode(@RequestBody InviteCodeForm inviteCodeForm) {
        Long memberId = inviteCodeForm.getMemberId();
        String inviteCode = inviteCodeForm.getInviteCode();
        boolean result = memberInviteService.setInviteCode(memberId, inviteCode);
        return Result.judge(result);
    }

}
