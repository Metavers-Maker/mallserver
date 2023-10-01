package com.muling.mall.ums.controller.app;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.muling.common.base.BasePageQuery;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.common.web.annotation.RequestLimit;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.ums.converter.MemberInviteConverter;
import com.muling.mall.ums.pojo.entity.UmsMemberInvite;
import com.muling.mall.ums.pojo.form.InviteCodeForm;
import com.muling.mall.ums.pojo.vo.MemberInviteVO;
import com.muling.mall.ums.service.IUmsMemberInviteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Api(tags = "app-会员邀请")
@RestController
@RequestMapping("/app-api/v1/members/invite")
@Slf4j
@RequiredArgsConstructor
public class MemberInviteController {

    private final IUmsMemberInviteService inviteService;

    @ApiOperation(value = "获得当前用户邀请人列表")
    @GetMapping("/page")
    public PageResult<MemberInviteVO> listInvitesWithPage(BasePageQuery pageQuery) {
        IPage<MemberInviteVO> result = inviteService.listInvitesByInviteMemberIdWithPage(pageQuery);
        return PageResult.success(result);
    }

    @ApiOperation(value = "获得用户邀请信息")
    @GetMapping
    @RequestLimit(count = 2, time = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result<MemberInviteVO> getMemberInfo() {
        Long memberId = MemberUtils.getMemberId();
        UmsMemberInvite memberInvite = inviteService.getInviteByMemberId(memberId);
        MemberInviteVO memberVO = MemberInviteConverter.INSTANCE.po2vo(memberInvite);
        return Result.success(memberVO);
    }

    @ApiOperation(value = "获得推荐人信息")
    @GetMapping("/referee")
    @RequestLimit(count = 2, time = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result<MemberInviteVO> getRefereeInfo() {
        Long memberId = MemberUtils.getMemberId();
        UmsMemberInvite referee = inviteService.getRefereeByMemberId(memberId);
        MemberInviteVO memberVO = MemberInviteConverter.INSTANCE.po2vo(referee);
        return Result.success(memberVO);
    }

    @ApiOperation(value = "添加邀请码")
    @PutMapping("/invite-code")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result addInviteCode(@RequestBody InviteCodeForm inviteCodeForm) {
        Long memberId = MemberUtils.getMemberId();
        String inviteCode = inviteCodeForm.getInviteCode();
        boolean result = inviteService.setInviteCode(memberId, inviteCode);
        return Result.judge(result);
    }
}
