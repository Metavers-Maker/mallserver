package com.muling.mall.wms.controller.rpc;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.mall.wms.pojo.dto.WalletDTO;
import com.muling.mall.wms.pojo.entity.WmsMarketConfig;
import com.muling.mall.wms.pojo.entity.WmsWallet;
import com.muling.mall.wms.service.IWmsWalletService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Api(tags = "rpc-钱包信息")
@RestController("RpcWalletController")
@RequestMapping("/app-api/v1/rpc/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final IWmsWalletService walletService;

    @ApiOperation(value = "更新余额")
    @PutMapping("/update")
    public Result<Boolean> updateBalance(@RequestBody WalletDTO walletDTO) {
        boolean result = walletService.updateBalance(walletDTO);
        return Result.judge(result);
    }

    @ApiOperation(value = "更新余额列表")
    @PutMapping("/update/list")
    public Result<Boolean> updateBalances(@RequestBody List<WalletDTO> list) {
        boolean result = walletService.updateBalances(list);
        return Result.judge(result);
    }

    @ApiOperation(value = "获得指定用户的指定币种的值")
    @GetMapping
    public Result<BigDecimal> getCoinValueByMemberIdAndCoinType(@RequestParam Long memberId, @RequestParam Integer coinType) {
        WmsWallet wallet = walletService.getCoinByMemberIdAndCoinType(memberId, coinType);
        if (wallet == null) {
            return Result.success(BigDecimal.ZERO);
        } else {
            return Result.success(wallet.getBalance());
        }
    }

    @ApiOperation(value = "封禁/解禁用户钱包")
    @GetMapping("/feng")
    public Result<Boolean> feng(@RequestParam Long memberId, @RequestParam Integer status) {
        boolean f = walletService.feng(memberId, status);
        return Result.judge(f);
    }

    @ApiOperation(value = "获取钱包积分排行")
    @GetMapping("/page")
    public PageResult page(
            @RequestParam Long pageNum,
            @RequestParam Long pageSize,
            @RequestParam Integer coinType) {
        LambdaQueryWrapper<WmsWallet> queryWrapper = new LambdaQueryWrapper<WmsWallet>()
                .eq(coinType != null, WmsWallet::getCoinType, coinType)
                .orderByDesc(WmsWallet::getBalance)
                .orderByDesc(WmsWallet::getUpdated)
                .orderByDesc(WmsWallet::getCreated);
        Page<WmsWallet> result = walletService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    //
}
