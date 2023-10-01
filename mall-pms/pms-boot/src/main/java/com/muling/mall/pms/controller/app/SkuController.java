package com.muling.mall.pms.controller.app;

import com.muling.common.result.Result;
import com.muling.mall.pms.pojo.vo.SkuVO;
import com.muling.mall.pms.es.service.PmsSkuEService;
import com.muling.mall.pms.service.IPmsSkuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品库存单元控制器 (Stock Keeping Unit)
 */
@Api(tags = "app-库存信息")
@RestController
@RequestMapping("/app-api/v1/sku")
@RequiredArgsConstructor
public class SkuController {

//    private final PmsSkuEService skuEService;

    private final IPmsSkuService skuService;

    @ApiOperation("获取商品库存数量")
    @GetMapping("/{skuId}/stock_num")
    public Result<Integer> getStockNum(
            @ApiParam("商品库存单元ID") @PathVariable Long skuId
    ) {
        Integer stockNum = skuService.getStockNum(skuId);
        return Result.success(stockNum);
    }

    @ApiOperation(value = "库存详情列表")
    @GetMapping
    public Result<List<SkuVO>> list(
            @ApiParam("商品ID数组") @RequestParam(required = true) List<Long> spuIds) {
        if (spuIds == null || spuIds.isEmpty() || spuIds.size() > 20) {
            return Result.failed("商品ID不能为空，且不能超过20个");
        }
        List<SkuVO> result = skuService.getAppSkuDetails(spuIds);
        return Result.success(result);
    }
}
