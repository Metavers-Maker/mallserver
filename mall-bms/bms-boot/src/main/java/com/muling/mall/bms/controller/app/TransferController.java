
package com.muling.mall.bms.controller.app;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.common.web.annotation.RequestLimit;
import com.muling.mall.bms.pojo.form.app.ItemTransferForm;
import com.muling.mall.bms.pojo.form.app.ItemTransferOutsideForm;
import com.muling.mall.bms.pojo.query.admin.TransferPageQuery;
import com.muling.mall.bms.pojo.vo.app.TransferVO;
import com.muling.mall.bms.service.IMemberItemService;
import com.muling.mall.bms.service.ITransferConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "app-转赠管理")
@RestController
@RequestMapping("/app-api/v1/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final ITransferConfigService transferConfigService;
    private final IMemberItemService memberItemService;

    @ApiOperation(value = "列表")
    @GetMapping("/page")
    public PageResult<TransferVO> list(TransferPageQuery queryParams) {

        IPage<TransferVO> page = transferConfigService.page(queryParams);
        return PageResult.success(page);
    }

    @ApiOperation(value = "转赠")
    @PostMapping
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result transfer(
            @Valid @RequestBody ItemTransferForm transferForm) {

        boolean transfer = memberItemService.transfer(transferForm);

        return Result.judge(transfer);
    }

    @ApiOperation(value = "转赠上链")
    @PostMapping("/outside")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result transferOutside(
            @Valid @RequestBody ItemTransferOutsideForm transferOutsideForm) {

        boolean transfer = memberItemService.transferOutside(transferOutsideForm);

        return Result.judge(transfer);
    }

}
