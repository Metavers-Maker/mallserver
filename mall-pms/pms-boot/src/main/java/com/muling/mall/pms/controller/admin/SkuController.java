package com.muling.mall.pms.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.annotation.AutoLog;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.mall.pms.common.enums.ViewTypeEnum;
import com.muling.mall.pms.pojo.entity.PmsSku;
import com.muling.mall.pms.pojo.form.SkuForm;
import com.muling.mall.pms.pojo.form.UpdateSkuForm;
import com.muling.mall.pms.pojo.query.SkuAdminPageQuery;
import com.muling.mall.pms.service.IPmsSkuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "admin-库存管理")
@RestController("SkuController")
@RequestMapping("/api/v1/sku")
@RequiredArgsConstructor
public class SkuController {

    private final IPmsSkuService skuService;


    @ApiOperation(value = "分页列表")
    @GetMapping("/page")
    public PageResult<PmsSku> page(
            SkuAdminPageQuery queryParams
    ) {
        LambdaQueryWrapper<PmsSku> wrapper = Wrappers.<PmsSku>lambdaQuery()
                .eq(queryParams.getSpuId() != null, PmsSku::getSpuId, queryParams.getSpuId())
                .orderByDesc(PmsSku::getUpdated);
        IPage<PmsSku> result = skuService.page(new Page<>(queryParams.getPageNum(), queryParams.getPageSize()), wrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "根据SPU_ID获取SKU列表")
    @GetMapping
    public Result<List<PmsSku>> list(
            @ApiParam("SPU ID") @RequestParam(required = true) List<Long> spuIds) {
        if (spuIds == null || spuIds.isEmpty() || spuIds.size() > 20) {
            return Result.failed("SPU ID不能为空，且不能超过20个");
        }
        LambdaQueryWrapper<PmsSku> wrapper = Wrappers.<PmsSku>lambdaQuery().in(PmsSku::getSpuId, spuIds);
        List<PmsSku> result = skuService.list(wrapper);
        return Result.success(result);
    }

    @ApiOperation(value = "新增")
    @PostMapping
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    public Result<Boolean> add(@RequestBody SkuForm skuForm) {

        boolean result = skuService.add(skuForm);

        return Result.success(result);
    }

    @ApiOperation(value = "修改库存信息，不包括数量")
    @PutMapping(value = "/{id}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result<Boolean> update(@ApiParam("skuId") @PathVariable Long id, @RequestBody UpdateSkuForm skuForm) {

        boolean result = skuService.update(id, skuForm);

        return Result.success(result);
    }

    @ApiOperation(value = "显示")
    @PutMapping("/display")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result display(
            @ApiParam("skuIds") @RequestParam(required = true) List<Long> skuIds) {
        boolean status = skuService.update(new LambdaUpdateWrapper<PmsSku>()
                .in(PmsSku::getId, skuIds)
                .eq(PmsSku::getVisible, ViewTypeEnum.INVISIBLE)
                .set(PmsSku::getVisible, ViewTypeEnum.VISIBLE));
        return Result.judge(status);
    }

    @ApiOperation(value = "隐藏")
    @PutMapping("/hide")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result hide(
            @ApiParam("skuIds") @RequestParam(required = true) List<Long> skuIds) {
        boolean status = skuService.update(new LambdaUpdateWrapper<PmsSku>()
                .in(PmsSku::getId, skuIds)
                .eq(PmsSku::getVisible, ViewTypeEnum.VISIBLE)
                .set(PmsSku::getVisible, ViewTypeEnum.INVISIBLE));
        return Result.judge(status);
    }

    @ApiOperation(value = "关闭交易")
    @PutMapping(value = "/{id}/closed")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result<Boolean> closed(@ApiParam("skuId") @PathVariable Long id) {

        boolean result = skuService.update(new LambdaUpdateWrapper<PmsSku>()
                .eq(PmsSku::getId, id)
                .eq(PmsSku::getClosed, 0)
                .set(PmsSku::getClosed, 1));

        return Result.success(result);
    }

    @ApiOperation(value = "打开交易")
    @PutMapping(value = "/{id}/opened")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result<Boolean> opened(@ApiParam("skuId") @PathVariable Long id) {

        boolean result = skuService.update(new LambdaUpdateWrapper<PmsSku>()
                .eq(PmsSku::getId, id)
                .eq(PmsSku::getClosed, 1)
                .set(PmsSku::getClosed, 0));

        return Result.success(result);
    }

    @ApiOperation(value = "商品库存详情")
    @GetMapping("/{skuId}")
    public Result get(
            @ApiParam("SKU ID") @PathVariable Long skuId
    ) {
        PmsSku sku = skuService.getById(skuId);
        return Result.success(sku);
    }

    @ApiOperation(value = "修改库存数量", notes = "实验室模拟", hidden = true)
    @PutMapping(value = "/{skuId}/stock_num")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result updateStockNum(
            @PathVariable Long skuId,
            @RequestParam Integer stockNum
    ) {
        boolean result = skuService.updateStockNum(skuId, stockNum);
        return Result.judge(result);
    }

    @ApiOperation(value = "扣减库存数量")
    @PutMapping(value = "/{skuId}/stock/_deduct")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result deductStock(
            @ApiParam("SKU ID") @PathVariable Long skuId,
            @ApiParam("订单SKu数量") @RequestParam Integer num,
            @ApiParam("盲盒Sku数量")  @RequestParam Integer rndNum
    ) {
        boolean result = skuService.deductStock(skuId, num, rndNum, true);
        return Result.judge(result);
    }
    @ApiOperation(value = "增加库存数量")
    @PutMapping(value = "/{skuId}/stock/_add")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result addStock(
            @ApiParam("SKU ID") @PathVariable Long skuId,
            @ApiParam("订单SKu数量") @RequestParam Integer num,
            @ApiParam("盲盒Sku数量")  @RequestParam Integer rndNum
    ) {
        boolean result = skuService.addStock(skuId, num, rndNum);
        return Result.judge(result);
    }
}
