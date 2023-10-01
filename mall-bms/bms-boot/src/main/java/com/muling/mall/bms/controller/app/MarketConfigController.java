
package com.muling.mall.bms.controller.app;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.common.web.annotation.RequestLimit;
import com.muling.mall.bms.pojo.query.app.MarketConfigPageQueryApp;
import com.muling.mall.bms.pojo.vo.app.MarketConfigVO;
import com.muling.mall.bms.service.IMarketConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "app-市场配置管理")
@RestController
@RequestMapping("/app-api/v1/market-config")
@RequiredArgsConstructor
public class MarketConfigController {

    private final IMarketConfigService marketConfigService;

    @ApiOperation(value = "列表")
    @GetMapping("/page")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public PageResult<MarketConfigVO> list(MarketConfigPageQueryApp queryParams) {

        IPage<MarketConfigVO> page = marketConfigService.page(queryParams);
        return PageResult.success(page);
    }

    @ApiOperation(value = "根据Spu获取寄售是否开通")
    @GetMapping("/isopen/{spuId}")
//    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result isOpen(
            @ApiParam("ids") @PathVariable(required = true) Long spuId) {
        boolean ret = marketConfigService.isOpen(spuId);
        return Result.judge(ret);
    }

//
}
