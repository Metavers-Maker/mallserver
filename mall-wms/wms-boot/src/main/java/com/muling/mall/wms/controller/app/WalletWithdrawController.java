package com.muling.mall.wms.controller.app;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.mall.wms.pojo.form.app.MarketCreateForm;
import com.muling.mall.wms.pojo.query.app.WithdrawPageQuery;
import com.muling.mall.wms.pojo.vo.WithdrawVO;
import com.muling.mall.wms.service.IWmsWithdrawService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import software.amazon.ion.Decimal;

import javax.validation.Valid;

@Api(tags = "app-提现")
@RestController
@RequestMapping("/app-api/v1/wallet/withdraw")
@RequiredArgsConstructor
public class WalletWithdrawController {

    private final IWmsWithdrawService wmsWithdrawService;

    @Operation(summary = "列表")
    @GetMapping
    public PageResult<WithdrawVO> list(WithdrawPageQuery queryParams) {
        IPage<WithdrawVO> page = wmsWithdrawService.page(queryParams);
        return PageResult.success(page);
    }

    @Operation(summary = "提现申请")
    @GetMapping("/apply")
    public Result<WithdrawVO> apply(@ApiParam("槽点") @RequestParam(defaultValue = "0") Decimal balance) {
        WithdrawVO withdrawVO = wmsWithdrawService.withdraw(balance,4);
        return Result.success(withdrawVO);
    }

    @Operation(summary = "提现取消")
    @GetMapping("/cancle")
    public Result<WithdrawVO> cancle(@ApiParam("槽点") @RequestParam(defaultValue = "0") Integer id) {
        boolean f = wmsWithdrawService.cancle(id);
        return Result.judge(f);
    }

    //
}
