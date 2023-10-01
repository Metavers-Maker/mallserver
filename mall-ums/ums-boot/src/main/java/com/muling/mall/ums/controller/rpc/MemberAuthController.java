package com.muling.mall.ums.controller.rpc;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.mall.ums.converter.MemberAuthConverter;
import com.muling.mall.ums.converter.SandAccountConverter;
import com.muling.mall.ums.pojo.app.MemberSearchDTO;
import com.muling.mall.ums.pojo.dto.*;
import com.muling.mall.ums.pojo.entity.UmsMember;
import com.muling.mall.ums.pojo.entity.UmsMemberAuth;
import com.muling.mall.ums.pojo.entity.UmsSandAccount;
import com.muling.mall.ums.service.ISandAccountService;
import com.muling.mall.ums.service.IUmsMemberAuthService;
import com.muling.mall.ums.service.IUmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "rpc-会员认证")
@RestController("RpcMemberAuthController")
@RequestMapping("/app-api/v1/rpc/auth")
@Slf4j
public class MemberAuthController {

    @Autowired
    private IUmsMemberAuthService memberAuthService;

    //    @Autowired
    private ISandAccountService sandAccountService;

    @ApiOperation(value = "根据会员ID获取Real信息")
    @GetMapping("/real/{memberId}")
    Result<MemberRealDTO> getMemberRealById(@PathVariable Long memberId) {
        //
        QueryWrapper<UmsMemberAuth> queryWrapper = new QueryWrapper<UmsMemberAuth>()
                .eq("member_id", memberId);
        UmsMemberAuth memberAuth = memberAuthService.getOne(queryWrapper);
        MemberRealDTO memberRealDTO = MemberAuthConverter.INSTANCE.po2dto(memberAuth);
        if (memberRealDTO == null) {
            return Result.failed(ResultCode.USER_NOT_EXIST);
        }
        return Result.success(memberRealDTO);
    }

    @ApiOperation(value = "根据会员ID获取Sand账户信息")
    @GetMapping("/sand/{memberId}")
    Result<MemberSandDTO> getMemberSandById(@PathVariable Long memberId) {
        //
        QueryWrapper<UmsSandAccount> queryWrapper = new QueryWrapper<UmsSandAccount>()
                .eq("member_id", memberId);
        UmsSandAccount sandAccount = sandAccountService.getOne(queryWrapper);
        if (sandAccount == null) {
            return Result.failed(ResultCode.USER_NOT_EXIST);
        }
        MemberSandDTO memberSandDTO = SandAccountConverter.INSTANCE.po2dto(sandAccount);
        return Result.success(memberSandDTO);
    }
    //
}
