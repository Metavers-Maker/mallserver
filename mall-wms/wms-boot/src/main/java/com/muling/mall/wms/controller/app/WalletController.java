package com.muling.mall.wms.controller.app;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.common.web.annotation.RequestLimit;
import com.muling.mall.wms.pojo.form.app.SwapForm;
import com.muling.mall.wms.pojo.form.app.TransferForm;
import com.muling.mall.wms.pojo.query.app.WalletPageQuery;
import com.muling.mall.wms.pojo.vo.WalletVO;
import com.muling.mall.wms.service.IWmsWalletService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "app-钱包信息")
@RestController
@RequestMapping("/app-api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final IWmsWalletService walletService;

    @ApiOperation(value = "列表")
    @GetMapping
    public PageResult<WalletVO> list(WalletPageQuery queryParams) {
        IPage<WalletVO> page = walletService.page(queryParams);
        return PageResult.success(page);
    }

    @ApiOperation(value = "积分兑换")
    @PostMapping
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result transfer(
            @Valid @RequestBody SwapForm swapForm) {
        WalletVO walletVO = walletService.swap(swapForm);
        return Result.success(walletVO);
    }

}
