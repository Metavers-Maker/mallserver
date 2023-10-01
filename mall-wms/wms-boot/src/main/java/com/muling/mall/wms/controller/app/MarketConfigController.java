
package com.muling.mall.wms.controller.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.common.web.annotation.RequestLimit;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.wms.converter.MarketConfigConverter;
import com.muling.mall.wms.pojo.entity.WmsMarketConfig;
import com.muling.mall.wms.pojo.query.app.MarketConfigPageQueryApp;
import com.muling.mall.wms.pojo.vo.MarketConfigVO;
import com.muling.mall.wms.service.IMarketConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
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

    @ApiOperation(value = "详细")
    @GetMapping("/detail")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result<MarketConfigVO> detail(@ApiParam(value = "积分类型", example = "0") Integer coinType,
                                           @ApiParam(value = "操作类型", example = "0") Integer opType) {
        WmsMarketConfig wmsMarketConfig = marketConfigService.getByCoinType(coinType,opType);
        Assert.isTrue(wmsMarketConfig!=null,"配置不存在");
        MarketConfigVO marketConfigVO = MarketConfigConverter.INSTANCE.do2vo(wmsMarketConfig);
        return Result.success(marketConfigVO);
    }
}
