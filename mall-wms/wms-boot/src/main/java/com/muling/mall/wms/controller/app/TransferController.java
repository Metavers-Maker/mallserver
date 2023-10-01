package com.muling.mall.wms.controller.app;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.muling.common.base.BasePageQuery;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.common.web.annotation.RequestLimit;
import com.muling.mall.wms.pojo.form.app.TransferForm;
import com.muling.mall.wms.pojo.query.app.TransferPageQuery;
import com.muling.mall.wms.pojo.vo.TransferLogVO;
import com.muling.mall.wms.pojo.vo.TransferVO;
import com.muling.mall.wms.pojo.vo.WalletVO;
import com.muling.mall.wms.service.IWmsTransferLogService;
import com.muling.mall.wms.service.IWmsTransferService;
import com.muling.mall.wms.service.IWmsWalletService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "app-转赠信息")
@RestController
@RequestMapping("/app-api/v1/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final IWmsTransferService transferService;
    private final IWmsTransferLogService transferLogService;

    @ApiOperation(value = "列表")
    @GetMapping("/page")
    public PageResult<TransferVO> list(TransferPageQuery queryParams) {

        IPage<TransferVO> page = transferService.page(queryParams);
        return PageResult.success(page);
    }

    @ApiOperation(value = "转赠")
    @PostMapping
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result transfer(
            @Valid @RequestBody TransferForm transferForm) {

        boolean transfer = transferService.transfer(transferForm);

        return Result.judge(transfer);
    }
}
