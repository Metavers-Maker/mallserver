package com.muling.mall.ums.controller.rpc;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.mall.ums.converter.MemberConverter;
import com.muling.mall.ums.pojo.app.MemberSearchDTO;
import com.muling.mall.ums.pojo.dto.*;
import com.muling.mall.ums.pojo.entity.UmsMember;
import com.muling.mall.ums.service.IUmsAccountChainService;
import com.muling.mall.ums.service.IUmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "rpc-会员")
@RestController("RpcMemberController")
@RequestMapping("/app-api/v1/rpc/members")
@Slf4j
public class MemberController {

    @Autowired
    private IUmsMemberService memberService;

    @Autowired
    private IUmsAccountChainService accountChainService;

    @GetMapping
    public Result<List<MemberSearchDTO>> list(Integer page, Integer limit, String name) {
        IPage<MemberSearchDTO> result = memberService.search(new Page<>(page, limit), name);

        return Result.success(result.getRecords(), result.getTotal());
    }

    @ApiOperation(value = "新增会员")
    @PostMapping
    public Result<Long> addMember(@RequestBody MemberDTO member) {
        Long memberId = memberService.addMember(member);
        return Result.success(memberId);
    }

    @ApiOperation(value = "绑定微信开放平台")
    @PostMapping("/bind/wxopen")
    public Result<Boolean> bindWxopen(@RequestBody BindWxopenDTO wxopenDTO) {
        boolean ret = memberService.bindWxopen(wxopenDTO);
        return Result.judge(ret);
    }

    @ApiOperation(value = "绑定支付宝")
    @PostMapping("/bind/alipay")
    public Result<Boolean> bindAlipay(@RequestBody BindAlipayDTO alipayDTO) {
        boolean ret = memberService.bindAlipay(alipayDTO);
        return Result.judge(ret);
    }

    @ApiOperation(value = "根据会员ID获取openid")
    @GetMapping("/{memberId}/openid")
    public Result<String> getMemberOpenId(@ApiParam("会员ID") @PathVariable Long memberId) {
        UmsMember member = memberService.getOne(
                new LambdaQueryWrapper<UmsMember>()
                        .eq(UmsMember::getId, memberId)
                        .select(UmsMember::getOpenid)
        );
        String openid = member.getOpenid();
        return Result.success(openid);
    }

    @GetMapping("/{memberId}/simple")
    public Result<MemberSimpleDTO> getSimpleUserById(@PathVariable Long memberId) {
        MemberSimpleDTO memberSimpleDTO = memberService.getSimpleById(memberId);
        if (memberSimpleDTO == null) {
            return Result.failed(ResultCode.USER_NOT_EXIST);
        }
        return Result.success(memberSimpleDTO);
    }

    @GetMapping("/{memberId}")
    public Result<MemberDTO> getMemberById(
            @ApiParam("会员ID") @PathVariable Long memberId) {
        UmsMember member = memberService.getById(memberId);
        if (member == null) {
            return Result.failed(ResultCode.USER_NOT_EXIST);
        }
        MemberDTO memberDTO = new MemberDTO();
        BeanUtil.copyProperties(member, memberDTO);
        return Result.success(memberDTO);
    }

    @GetMapping("/mobile/{mobile}")
    public Result<MemberAuthDTO> getByMobile(
            @ApiParam("手机号码") @PathVariable String mobile) {
        MemberAuthDTO memberAuthInfo = memberService.getByMobile(mobile);
        if (memberAuthInfo == null) {
            return Result.failed(ResultCode.USER_NOT_EXIST);
        }
        return Result.success(memberAuthInfo);
    }

    @GetMapping("/email/{email}")
    public Result<MemberAuthDTO> getByEmail(
            @ApiParam("邮箱") @PathVariable String email) {

        MemberAuthDTO memberAuthInfo = memberService.getByEmail(email);
        if (memberAuthInfo == null) {
            return Result.failed(ResultCode.USER_NOT_EXIST);
        }
        return Result.success(memberAuthInfo);
    }

    @GetMapping("/username/{username}")
    public Result<MemberAuthDTO> getByUsername(
            @ApiParam("用户名") @PathVariable String username) {
        MemberAuthDTO memberAuthInfo = memberService.getByUsername(username);
        if (memberAuthInfo == null) {
            return Result.failed(ResultCode.USER_NOT_EXIST);
        }
        return Result.success(memberAuthInfo);
    }

    @GetMapping("/uid/{uid}")
    public Result<MemberAuthDTO> getByUid(@PathVariable String uid) {
        MemberAuthDTO memberAuthInfo = memberService.getByUId(uid);
        if (memberAuthInfo == null) {
            return Result.failed(ResultCode.USER_NOT_EXIST);
        }
        return Result.success(memberAuthInfo);
    }

    @GetMapping("/openid/{openid}")
    public Result<MemberAuthDTO> getByOpenid(
            @ApiParam("微信身份标识") @PathVariable String openid
    ) {
        MemberAuthDTO memberAuthInfo = memberService.getByOpenid(openid);
        if (memberAuthInfo == null) {
            return Result.failed(ResultCode.USER_NOT_EXIST);
        }
        return Result.success(memberAuthInfo);
    }

    @GetMapping("/alipayid/{alipayid}")
    public Result<MemberAuthDTO> getByAlipayId(
            @ApiParam("支付宝身份标识") @PathVariable String alipayid
    ) {
        MemberAuthDTO memberAuthInfo = memberService.getByAlipayId(alipayid);
        if (memberAuthInfo == null) {
            return Result.failed(ResultCode.USER_NOT_EXIST);
        }
        return Result.success(memberAuthInfo);
    }

    @ApiOperation("用户列表按时间查询")
    @GetMapping("/list")
    public Result list(@RequestParam String begin, @RequestParam String end) {
        Assert.notNull(begin);
        Assert.notNull(end);
        List<UmsMember> members = memberService.list(new LambdaQueryWrapper<UmsMember>().ge(UmsMember::getCreated, begin).lt(UmsMember::getCreated, end));

        if (members != null && !members.isEmpty()) {
            return Result.success(members.stream().map(MemberConverter.INSTANCE::po2dto).collect(Collectors.toList()));
        }
        return Result.success();
    }

    @ApiOperation("用户列表按Ids查询")
    @PostMapping("/listByIds")
    public Result byIds(@RequestBody MemberListByIds input) {
        Assert.notNull(input.getMemberIds());
        List<UmsMember> members = memberService.list(new LambdaQueryWrapper<UmsMember>().in(UmsMember::getId, input.getMemberIds()));

        if (members != null && !members.isEmpty()) {
            return Result.success(members.stream().map(MemberConverter.INSTANCE::po2dto).collect(Collectors.toList()));
        }
        return Result.success();
    }

}
