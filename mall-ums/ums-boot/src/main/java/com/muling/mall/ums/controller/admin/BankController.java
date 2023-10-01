package com.muling.mall.ums.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.annotation.AutoLog;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.common.web.annotation.RequestLimit;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.ums.pojo.entity.UmsBank;
import com.muling.mall.ums.pojo.entity.UmsMember;
import com.muling.mall.ums.pojo.entity.UmsWhite;
import com.muling.mall.ums.pojo.form.AdminRegisterForm;
import com.muling.mall.ums.pojo.form.BankBindEnsureForm;
import com.muling.mall.ums.pojo.form.BankBindForm;
import com.muling.mall.ums.pojo.vo.BankBindVO;
import com.muling.mall.ums.service.IUmsBankService;
import com.muling.mall.ums.service.IUmsMemberService;
import com.muling.mall.ums.service.IUmsWhiteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Api(tags = "admin-银行卡")
@RestController("AdminBankController")
@RequestMapping("/api/v1/bank")
@Slf4j
@AllArgsConstructor
public class BankController {

    private IUmsMemberService memberService;

    private IUmsBankService bankService;

    @ApiOperation(value = "分页列表")
    @GetMapping("/page")
    public PageResult<UmsBank> listWithPage(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "会员ID") Long memberId,
            @ApiParam(value = "开始时间") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime started,
            @ApiParam(value = "结束时间") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime ended
    ) {
        LambdaQueryWrapper<UmsBank> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(memberId != null, UmsBank::getMemberId, memberId);
        wrapper.ge(started != null, UmsBank::getCreated, started);
        wrapper.le(ended != null, UmsBank::getCreated, ended);
        wrapper.orderByDesc(UmsBank::getCreated);
        IPage<UmsBank> result = bankService.page(new Page<>(pageNum, pageSize), wrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "申请绑定银行卡")
    @PostMapping("/bind/apply/{memberId}")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.CUSTOM_VALUE, field = "mobile")
    public Result bindApply(
            @Validated @PathVariable Long memberId,
            @Validated @RequestBody BankBindForm bindForm) {
        BankBindVO bindVO = bankService.bindCard(bindForm, memberId);
        return Result.success(bindVO);
    }

    @ApiOperation(value = "确认绑定银行卡")
    @PostMapping("/bind/ensure")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.CUSTOM_VALUE, field = "mobile")
    public Result bindEnsure(
            @Validated @PathVariable Long memberId,
            @Validated @RequestBody BankBindEnsureForm bindEnsureForm) {
        boolean ret = bankService.bindCardEnsure(bindEnsureForm, memberId);
        return Result.judge(ret);
    }

    @ApiOperation(value = "解绑银行卡")
    @PostMapping("/unbind/{memberId}/{id}")
//    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.CUSTOM_VALUE, field = "mobile")
    public Result unbind(
            @Validated @PathVariable Long memberId,
            @Validated @PathVariable Long id) {
        boolean result = bankService.unbindCard(id, memberId);
        return Result.judge(result);
    }

    @ApiOperation(value = "设置默认银行卡")
    @PostMapping("/userd/{memberId}/{id}")
    public Result usedBank(
            @Validated @PathVariable Long memberId,
            @Validated @PathVariable Long id) {
        boolean result = bankService.usedBank(id, memberId);
        return Result.judge(result);
    }


}
