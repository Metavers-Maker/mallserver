package com.muling.mall.ums.controller.rpc;

import cn.hutool.core.bean.BeanUtil;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.mall.ums.pojo.dto.*;
import com.muling.mall.ums.pojo.entity.UmsMemberInvite;
import com.muling.mall.ums.service.IUmsMemberInviteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "rpc-会员邀请")
@RestController("RpcMemberInviteController")
@RequestMapping("/app-api/v1/rpc/members/invite")
@Slf4j
public class MemberInviteController {

    @Resource
    private IUmsMemberInviteService memberInviteService;

    @GetMapping("/{memberId}")
    public Result<MemberInviteDTO> getMemberInviteById(@PathVariable Long memberId) {
        UmsMemberInvite memberInvite = memberInviteService.getInviteByMemberId(memberId);
        if (memberInvite == null) {
            return Result.failed(ResultCode.USER_NOT_EXIST);
        }
        MemberInviteDTO memberDTO = new MemberInviteDTO();
        BeanUtil.copyProperties(memberInvite, memberDTO);
        return Result.success(memberDTO);
    }

    @GetMapping("/depth/{memberId}")
    public Result<List<MemberInviteDTO>> getMemberInviteDepthById(@PathVariable Long memberId) {
        ArrayList<MemberInviteDTO> arrayList = new ArrayList<>();
        return Result.success(arrayList);
    }

    @ApiOperation(value = "星级分红")
    @PutMapping("/star-dispatch")
    public Result<Boolean> starDispatch(@RequestBody StarDispatchDTO starDispatch) {
        boolean b = memberInviteService.starDispatch(starDispatch.getStar(),starDispatch.getFee());
        return Result.judge(b);
    }

    @ApiOperation(value = "小任务分红")
    @PutMapping("/ad-mission-dispatch")
    public Result<Boolean> adMissionDispatch(@RequestBody AdMissionDispatchDTO adMissionDispatchDTO) {
        boolean b = memberInviteService.adMissionDispatch(adMissionDispatchDTO);
        return Result.judge(b);
    }

    @ApiOperation(value = "Ad农场分红")
    @PutMapping("/ad-farm-dispatch")
    public Result<Boolean> adFarmDispatch(@RequestBody AdFarmDispatchDTO adFarmDispatchDTO) {
        boolean b = memberInviteService.adFarmDispatch(adFarmDispatchDTO);
        return Result.judge(b);
    }

    @ApiOperation(value = "Game农场分红")
    @PutMapping("/game-farm-dispatch")
    public Result<Boolean> gameFarmDispatch(@RequestBody AdFarmDispatchDTO adFarmDispatchDTO) {
        boolean b = memberInviteService.gameFarmDispatch(adFarmDispatchDTO);
        return Result.judge(b);
    }

}
