package com.muling.mall.ums.controller.admin;

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
import com.muling.mall.ums.pojo.entity.UmsAccountChain;
import com.muling.mall.ums.pojo.form.AddressChainForm;
import com.muling.mall.ums.service.IUmsAccountChainService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Api(tags = "admin-链上账号")
@RestController("AdminAccountChainController")
@RequestMapping("/api/v1/chain/account")
@Slf4j
@AllArgsConstructor
public class AccountChainController {

    private IUmsAccountChainService umsAccountChainService;

    @ApiOperation(value = "分页列表")
    @GetMapping("/page")
    public PageResult<UmsAccountChain> page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "会员ID") Long memberId,
            @ApiParam(value = "地址类型") Integer chainType,
            @ApiParam(value = "开始时间") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime started,
            @ApiParam(value = "结束时间") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime ended
    ) {
        //inviteNum
        LambdaQueryWrapper<UmsAccountChain> wrapper = Wrappers.<UmsAccountChain>lambdaQuery();
        wrapper.eq(memberId != null, UmsAccountChain::getMemberId, memberId);
        wrapper.eq(chainType != null, UmsAccountChain::getChainType, chainType);
        wrapper.ge(started != null, UmsAccountChain::getCreated, started);
        wrapper.le(ended != null, UmsAccountChain::getCreated, ended);
        wrapper.orderByDesc(UmsAccountChain::getChainType);
        wrapper.orderByDesc(UmsAccountChain::getCreated);
        IPage<UmsAccountChain> result = umsAccountChainService.page(new Page(pageNum, pageSize), wrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "生成BSN账户")
    @PostMapping("/bsn/gen/{memberId}")
//    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.REGIST)
    public Result<String> genBsnAccount(@PathVariable Long memberId) {
        return umsAccountChainService.genBsnAccountByMemberId(memberId);
    }

    @ApiOperation(value = "同步BSN账户")
    @PostMapping("/bsn/sync/{memberId}")
//    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.REGIST)
    public Result<Boolean> syncBsnAccount(@PathVariable Long memberId) {
        boolean ret = umsAccountChainService.syncBsnAccount(memberId);
        return Result.judge(ret);
    }

    @ApiOperation(value = "绑定三方账户")
    @PostMapping("/bind/{memberId}")
    @RequestLimit(count = 5, waits = 1, limitFiledType = RequestLimit.LimitFiledType.IP)
    public Result bindAccount(
            @PathVariable Long memberId,
            @RequestBody @Validated AddressChainForm addressForm) {
        boolean ret = umsAccountChainService.bindAccount(addressForm, memberId);
        return Result.judge(ret);
    }

    @ApiOperation(value = "更新三方账户")
    @PutMapping("/update/{id}/{memberId}")
    @RequestLimit(count = 5, waits = 1, limitFiledType = RequestLimit.LimitFiledType.IP)
    public Result updateAccount(
            @PathVariable Long id,
            @PathVariable Long memberId,
            @RequestBody @Validated AddressChainForm addressForm
    ) {
        boolean ret = umsAccountChainService.updateAccount(id, addressForm, memberId);
        return Result.judge(ret);
    }

    @ApiOperation(value = "删除三方账户")
    @DeleteMapping("/del/{ids}")
    @RequestLimit(count = 5, waits = 1, limitFiledType = RequestLimit.LimitFiledType.IP)
    public Result deleteAccount(@ApiParam("id集合") @PathVariable("ids") String ids) {
        boolean status = umsAccountChainService.remove(new LambdaQueryWrapper<UmsAccountChain>()
                .in(UmsAccountChain::getId, ids.split(",")));
        return Result.judge(status);
    }

    //
}
