package com.muling.mall.ums.controller.app;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.muling.common.result.Result;
import com.muling.common.web.annotation.RequestLimit;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.ums.es.service.IUmsMemberEService;
import com.muling.mall.ums.pojo.app.MemberFormDTO;
import com.muling.mall.ums.pojo.entity.UmsMember;
import com.muling.mall.ums.pojo.vo.MemberSimpleVO;
import com.muling.mall.ums.pojo.vo.MemberVO;
import com.muling.mall.ums.service.IUmsMemberAuthService;
import com.muling.mall.ums.service.IUmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Api(tags = "app-会员")
@RestController
@RequestMapping("/app-api/v1/members")
@Slf4j
@RequiredArgsConstructor
public class MemberController {

    private final IUmsMemberService umsMemberService;
    private final IUmsMemberAuthService umsMemberAuthService;
    private final IUmsMemberEService umsMemberEService;

    @ApiOperation(value = "获得用户信息")
    @GetMapping("/me")
//    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result<MemberVO> getMemberInfo() {
        MemberVO memberVO = umsMemberService.getCurrentMemberInfo();
        return Result.success(memberVO);
    }

    @ApiOperation(value = "根据UID获得指定用户信息")
    @GetMapping("/{uid}")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result<MemberSimpleVO> getMemberInfo(@PathVariable String uid) {
        MemberSimpleVO memberVO = umsMemberService.getMemberInfoByUid(uid);
        return Result.success(memberVO);
    }

    @ApiOperation(value = "根据ID获得指定用户信息")
    @GetMapping("/id/{id}")
    public Result<MemberSimpleVO> getMemberInfo(@PathVariable Long id) {
        MemberSimpleVO memberVO = umsMemberService.getMemberInfoById(id);
        return Result.success(memberVO);
    }

    //count = 5, time = 24 * 3600, waits = 5,
    @ApiOperation(value = "修改用户信息")
    @PutMapping("/me")
    @RequestLimit(limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result updateMember(@RequestBody MemberFormDTO memberFormDTO) {
        Long memberId = MemberUtils.getMemberId();
        LambdaUpdateWrapper<UmsMember> updateWrapper = Wrappers.<UmsMember>lambdaUpdate()
                .set(StrUtil.isNotBlank(memberFormDTO.getNickName()), UmsMember::getNickName, memberFormDTO.getNickName())
                .set(StrUtil.isNotBlank(memberFormDTO.getAvatarUrl()), UmsMember::getAvatarUrl, memberFormDTO.getAvatarUrl())
                .set(StrUtil.isNotBlank(memberFormDTO.getSafeCode()), UmsMember::getSafeCode, memberFormDTO.getSafeCode())
                .set(memberFormDTO.getExt()!=null, UmsMember::getExt, memberFormDTO.getExt())
                .eq(UmsMember::getId, memberId);
        boolean status = umsMemberService.update(updateWrapper);
        return Result.judge(status);
    }

    @ApiOperation(value = "绑定区块链地址信息")
    @PutMapping("/me/address")
    @RequestLimit(limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result updateMemberAddress(@RequestParam String address) {
        Long memberId = MemberUtils.getMemberId();
        LambdaUpdateWrapper<UmsMember> updateWrapper = Wrappers.<UmsMember>lambdaUpdate()
                .set(address!=null, UmsMember::getChainAddress, address)
                .eq(UmsMember::getId, memberId);
        boolean status = umsMemberService.update(updateWrapper);
        return Result.judge(status);
    }

}
