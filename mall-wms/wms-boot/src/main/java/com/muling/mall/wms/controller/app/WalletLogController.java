package com.muling.mall.wms.controller.app;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.muling.common.base.BasePageQuery;
import com.muling.common.result.PageResult;
import com.muling.mall.wms.pojo.query.app.WalletLogPageQuery;
import com.muling.mall.wms.pojo.vo.WalletLogVO;
import com.muling.mall.wms.service.IWmsWalletLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "app-钱包日志信息")
@RestController
@RequestMapping("/app-api/v1/wallet-log")
@RequiredArgsConstructor
public class WalletLogController {

    private final IWmsWalletLogService walletLogService;

    @Operation(summary = "列表")
    @GetMapping
    public PageResult<WalletLogVO> list(WalletLogPageQuery queryParams) {
        IPage<WalletLogVO> page = walletLogService.page(queryParams);
        return PageResult.success(page);
    }

}
