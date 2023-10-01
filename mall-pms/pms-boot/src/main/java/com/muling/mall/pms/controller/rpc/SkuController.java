package com.muling.mall.pms.controller.rpc;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.muling.common.result.Result;
import com.muling.mall.pms.converter.SkuConverter;
import com.muling.mall.pms.pojo.dto.CheckPriceDTO;
import com.muling.mall.pms.pojo.dto.RndDTO;
import com.muling.mall.pms.pojo.dto.SkuInfoDTO;
import com.muling.mall.pms.pojo.dto.app.LockStockDTO;
import com.muling.mall.pms.pojo.entity.PmsRnd;
import com.muling.mall.pms.pojo.entity.PmsSku;
import com.muling.mall.pms.pojo.entity.PmsSpu;
import com.muling.mall.pms.service.IPmsRndService;
import com.muling.mall.pms.service.IPmsSkuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "rpc-库存信息")
@RestController("RpcSkuController")
@RequestMapping("/app-api/v1/rpc/sku")
@RequiredArgsConstructor
public class SkuController {

    private final IPmsSkuService skuService;

    private final IPmsRndService rndService;

    @ApiOperation(value = "获取商品库存信息")
    @GetMapping("/{skuId}/info")
    public Result<SkuInfoDTO> getSkuInfo(
            @ApiParam("SKU ID") @PathVariable Long skuId
    ) {
        SkuInfoDTO skuInfo = skuService.getSkuInfo(skuId);
        return Result.success(skuInfo);
    }

    @ApiOperation(value = "锁定库存")
    @PutMapping("/_lock")
    public Result lockStock(@RequestBody LockStockDTO lockStockDTO) {
        boolean lockResult = skuService.lockStock(lockStockDTO);
        return Result.success(lockResult);
    }

    @ApiOperation(value = "解锁库存")
    @PutMapping("/_unlock")
    public Result<Boolean> unlockStock(String orderToken) {
        boolean result = skuService.unlockStock(orderToken);
        return Result.judge(result);
    }

    @ApiOperation(value = "扣减库存")
    @PutMapping("/_deduct")
    public Result<Boolean> deductStock(String orderToken) {
        boolean result = skuService.deductStock(orderToken);
        return Result.judge(result);
    }

    @ApiOperation(value = "扣减用于开盲盒的库存")
    @PutMapping("/{skuId}/_deduct/rnd")
    public Result<Boolean> deductStock(@PathVariable Long skuId, @RequestParam Integer rndStockNum) {
        boolean result = skuService.deductStock(skuId, 0, rndStockNum, false);
        return Result.judge(result);
    }

    @ApiOperation(value = "增加铸造")
    @PutMapping("/{skuId}/_mint")
    public Result<Boolean> deductMint(@PathVariable Long skuId, @RequestParam Integer num) {
        boolean result = skuService.deductMint(skuId, num);
        return Result.judge(result);
    }

    @ApiOperation(value = "商品验价")
    @PostMapping("/price/_check")
    public Result<Boolean> checkPrice(@RequestBody CheckPriceDTO checkPriceDTO) {
        boolean result = skuService.checkPrice(checkPriceDTO);
        return Result.success(result);
    }

    @ApiOperation(value = "获取商品盲盒规则信息")
    @GetMapping("/{skuId}/rnd-info")
    public Result<List<RndDTO>> getSkuRndInfo(
            @ApiParam("SKU ID") @PathVariable Long skuId
    ) {
        List<PmsRnd> pmsRndList = rndService.list(Wrappers.<PmsRnd>lambdaQuery().eq(PmsRnd::getSkuId, skuId));
        if (pmsRndList.isEmpty()) {
            return Result.failed("盲盒规则不存在");
        }
        List<RndDTO> rndDTOList = new ArrayList<>();
        pmsRndList.forEach(item->{
            RndDTO dto = new RndDTO();
            dto.setId(item.getId());
            dto.setTarget(item.getTarget());
            dto.setSpuId(item.getSpuId());
            dto.setSpuCount(item.getSpuCount());
            dto.setSkuId(item.getSkuId());
            dto.setProd(item.getProd());
            dto.setCoinType(item.getCoinType());
            dto.setCoinCount(item.getCoinCount());
            rndDTOList.add(dto);
        });
        return Result.success(rndDTOList);
    }

    @ApiOperation("列表按时间查询")
    @GetMapping("/list")
    public Result<List<SkuInfoDTO>> list(@RequestParam String begin, @RequestParam String end) {
        Assert.notNull(begin);
        Assert.notNull(end);

        List<PmsSku> pmsSkus = skuService.list(new LambdaQueryWrapper<PmsSku>().ge(PmsSku::getCreated, begin).lt(PmsSku::getCreated, end));


        if (pmsSkus != null && !pmsSkus.isEmpty()) {
            return Result.success(pmsSkus.stream().map(SkuConverter.INSTANCE::do2dto).collect(Collectors.toList()));
        }
        return Result.success();
    }
}
