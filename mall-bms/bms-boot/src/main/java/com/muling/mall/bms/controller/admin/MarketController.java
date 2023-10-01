package com.muling.mall.bms.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.mall.bms.enums.ItemTypeEnum;
import com.muling.mall.bms.enums.MarketStatusEnum;
import com.muling.mall.bms.pojo.entity.OmsMarket;
import com.muling.mall.bms.service.IMarketService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = "admin-市场")
@RestController("MarketController")
@RequestMapping("/api/v1/market")
@RequiredArgsConstructor
public class MarketController {

    private final IMarketService marketService;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "物品ID") Long itemId,
            @ApiParam(value = "名称") String name,
            @ApiParam(value = "spuId") Long spuId,
            @ApiParam(value = "物品类型", allowMultiple = true) ItemTypeEnum[] itemTypes,
            @ApiParam(value = "状态") MarketStatusEnum status,
            @ApiParam(value = "排序") String orderBy,
            @ApiParam(value = "排序方式") boolean isAsc
    ) {

        QueryWrapper<OmsMarket> queryWrapper = new QueryWrapper<OmsMarket>()
                .like(StrUtil.isNotBlank(name), "name", name)
                .eq(itemId != null, "item_id", itemId)
                .eq(spuId != null, "spu_id", spuId)
                .eq(status != null, "status", status)
                .in(itemTypes != null && itemTypes.length > 0, "item_type", itemTypes)
                .orderBy(StrUtil.isNotBlank(orderBy), isAsc, orderBy)
                .orderByDesc("updated");
        Page<OmsMarket> result = marketService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    //
    @ApiOperation(value = "后台撤销")
    @PostMapping("/cancle/{marketId}")
    public Result<Boolean> cancle(@ApiParam("id") @PathVariable Long marketId) {
        boolean ret = marketService.adminCancle(marketId);
        return Result.judge(ret);
    }

}
