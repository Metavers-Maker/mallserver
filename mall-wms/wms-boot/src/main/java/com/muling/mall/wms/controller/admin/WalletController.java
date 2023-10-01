package com.muling.mall.wms.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.annotation.AutoLog;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.mall.wms.common.enums.CoinStatusEnum;
import com.muling.mall.wms.pojo.entity.WmsWallet;
import com.muling.mall.wms.pojo.form.admin.WalletForm;
import com.muling.mall.wms.service.IWmsWalletService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Api(tags = "admin-钱包管理")
@RestController("WalletController")
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final IWmsWalletService walletService;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "会员ID") Long memberId,
            @ApiParam(value = "从大到小排序") Integer orderBalance,
            @ApiParam(value = "类型", allowMultiple = true) Integer[] coinTypes) {

        LambdaQueryWrapper<WmsWallet> queryWrapper = new LambdaQueryWrapper<WmsWallet>()
                .eq(memberId != null, WmsWallet::getMemberId, memberId)
                .in(coinTypes != null && coinTypes.length > 0, WmsWallet::getCoinType, coinTypes)
                .orderByDesc(orderBalance!=null,WmsWallet::getBalance)
                .orderByDesc(WmsWallet::getUpdated)
                .orderByDesc(WmsWallet::getCreated);
        Page<WmsWallet> result = walletService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "列表分页搜索")
    @GetMapping("/page/search")
    public PageResult pageSearch(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "类型") Integer coinType,
            @ApiParam(value = "最小值") Integer minBalance,
            @ApiParam(value = "最大值") Integer maxBalance,
            @ApiParam(value = "从大到小排序") Integer orderBalance) {

        LambdaQueryWrapper<WmsWallet> queryWrapper = new LambdaQueryWrapper<WmsWallet>()
                .eq(coinType != null, WmsWallet::getCoinType, coinType)
                .ge(minBalance != null , WmsWallet::getBalance, minBalance)
                .le(maxBalance != null , WmsWallet::getBalance, maxBalance)
                .orderByDesc(orderBalance!=null,WmsWallet::getBalance)
                .orderByDesc(WmsWallet::getUpdated)
                .orderByDesc(WmsWallet::getCreated);
        Page<WmsWallet> result = walletService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "更新余额")
    @PostMapping("/up")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result updateBalance(
            @RequestBody WalletForm walletForm) {
        boolean status = walletService.save(walletForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "设置可用")
    @PutMapping("/enabled")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result enabled(
            @ApiParam("wallet ids") @RequestParam(required = true) List<Long> walletIds) {
        boolean status = walletService.update(new LambdaUpdateWrapper<WmsWallet>()
                .in(WmsWallet::getId, walletIds)
                .eq(WmsWallet::getStatus, CoinStatusEnum.DISABLED)
                .set(WmsWallet::getStatus, CoinStatusEnum.ENABLED));
        return Result.judge(status);
    }

    @ApiOperation(value = "设置禁用")
    @PutMapping("/disabled")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result hide(
            @ApiParam("wallet ids") @RequestParam(required = true) List<Long> walletIds) {
        boolean status = walletService.update(new LambdaUpdateWrapper<WmsWallet>()
                .in(WmsWallet::getId, walletIds)
                .eq(WmsWallet::getStatus, CoinStatusEnum.ENABLED)
                .set(WmsWallet::getStatus, CoinStatusEnum.DISABLED));
        return Result.judge(status);
    }

    @ApiOperation(value = "获取积分总量")
    @GetMapping("/total")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result total(
            @ApiParam("coin type") @RequestParam(required = true) Integer coinType) {
        BigDecimal total = walletService.total(coinType);
        return Result.success(total);
    }

}
