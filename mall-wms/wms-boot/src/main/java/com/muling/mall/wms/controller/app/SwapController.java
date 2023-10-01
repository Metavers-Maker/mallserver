package com.muling.mall.wms.controller.app;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.common.web.annotation.RequestLimit;
import com.muling.mall.wms.pojo.form.app.NewSwapForm;
import com.muling.mall.wms.pojo.query.app.SwapConfigPageQuery;
import com.muling.mall.wms.pojo.vo.SwapConfigVO;
import com.muling.mall.wms.service.IWmsSwapService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "app-兑换信息")
@RestController
@RequestMapping("/app-api/v1/swap")
@RequiredArgsConstructor
public class SwapController {

    private final IWmsSwapService swapService;

    @ApiOperation(value = "列表")
    @GetMapping("/page")
    public PageResult<SwapConfigVO> list(SwapConfigPageQuery queryParams) {

        IPage<SwapConfigVO> page = swapService.page(queryParams);
        return PageResult.success(page);
    }

    @ApiOperation(value = "兑换")
    @PostMapping
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result swap(
            @Valid @RequestBody NewSwapForm swapForm) {

        boolean transfer = swapService.swap(swapForm);

        return Result.judge(transfer);
    }
}
