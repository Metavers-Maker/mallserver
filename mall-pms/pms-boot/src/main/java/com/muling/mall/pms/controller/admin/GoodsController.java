package com.muling.mall.pms.controller.admin;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.muling.common.annotation.AutoLog;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.mall.pms.common.enums.StatusEnum;
import com.muling.mall.pms.common.enums.ViewTypeEnum;
import com.muling.mall.pms.pojo.entity.PmsSpu;
import com.muling.mall.pms.pojo.form.GoodsFormDTO;
import com.muling.mall.pms.pojo.query.SpuAdminPageQuery;
import com.muling.mall.pms.service.IPmsBsnService;
import com.muling.mall.pms.service.IPmsSpuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "admin-NFT商品管理")
@RestController
@RequestMapping("/api/v1/goods")
@AllArgsConstructor
public class GoodsController {

    private IPmsSpuService spuService;

    private IPmsBsnService pmsBsnService;

    @ApiOperation(value = "分页列表")
    @GetMapping("/page")
    public PageResult<PmsSpu> list(
            SpuAdminPageQuery queryParams
    ) {
        IPage<PmsSpu> result = spuService.listAdminSpuPage(queryParams);
        return PageResult.success(result);
    }

    @ApiOperation(value = "根据SPU_ID获取SPU列表")
    @GetMapping("/ids")
    public Result<List<PmsSpu>> listBySpuIds(
            @ApiParam("SPU ID") @RequestParam(required = true) List<Long> spuIds) {
        if (spuIds == null || spuIds.isEmpty() || spuIds.size() > 20) {
            return Result.failed("SKU ID不能为空，且不能超过20个");
        }

        List<PmsSpu> result = spuService.list(Wrappers.<PmsSpu>lambdaQuery().in(PmsSpu::getId, spuIds));
        return Result.success(result);
    }

    @ApiOperation(value = "商品详情")
    @GetMapping("/{spuId}")
    public Result<PmsSpu> detail(
            @ApiParam("商品ID") @PathVariable Long spuId) {
        PmsSpu spu = spuService.getById(spuId);
        if (spu == null) {
            return Result.failed("商品不存在");
        }
        return Result.success(spu);
    }

    @ApiOperation(value = "新增商品")
    @PostMapping
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    public Result addGoods(
            @RequestBody GoodsFormDTO goodsForm) {
        boolean result = spuService.addGoods(goodsForm);
        return Result.judge(result);
    }

    @ApiOperation(value = "修改商品")
    @PutMapping(value = "/{id}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result update(
            @ApiParam("商品ID") @PathVariable Long id,
            @RequestBody GoodsFormDTO goods) {
        boolean result = spuService.updateGoods(id, goods);
        return Result.judge(result);
    }

    @ApiOperation(value = "上架")
    @PutMapping("/up")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result up(
            @ApiParam("spu ids") @RequestParam(required = true) List<Long> spuIds) {
        boolean status = spuService.update(new LambdaUpdateWrapper<PmsSpu>()
                .in(PmsSpu::getId, spuIds)
                .eq(PmsSpu::getStatus, StatusEnum.DOWN)
                .set(PmsSpu::getStatus, StatusEnum.UP));
        return Result.judge(status);
    }

    @ApiOperation(value = "显示")
    @PutMapping("/display")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result display(
            @ApiParam("subject ids") @RequestParam(required = true) List<Long> spuIds) {
        boolean status = spuService.update(new LambdaUpdateWrapper<PmsSpu>()
                .in(PmsSpu::getId, spuIds)
                .eq(PmsSpu::getStatus, StatusEnum.UP)
                .set(PmsSpu::getVisible, ViewTypeEnum.VISIBLE));
        return Result.judge(status);
    }

    @ApiOperation(value = "隐藏")
    @PutMapping("/hide")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result hide(
            @ApiParam("subject ids") @RequestParam(required = true) List<Long> spuIds) {
        boolean status = spuService.update(new LambdaUpdateWrapper<PmsSpu>()
                .in(PmsSpu::getId, spuIds)
                .eq(PmsSpu::getVisible, ViewTypeEnum.VISIBLE)
                .set(PmsSpu::getVisible, ViewTypeEnum.INVISIBLE));
        return Result.judge(status);
    }

    @ApiOperation(value = "删除商品")
    @DeleteMapping("/{ids}")
    @AutoLog(operateType = LogOperateTypeEnum.DELETE, logType = LogTypeEnum.OPERATE)
    public Result delete(
            @ApiParam("id集合,以英文逗号(,)分隔") @PathVariable String ids) {
        boolean result = spuService.removeByGoodsIds(Arrays.asList(ids.split(","))
                .stream().map(id -> Long.parseLong(id))
                .collect(Collectors.toList()));
        return Result.judge(result);
    }

    @ApiOperation(value = "铸造NFT商品")
    @PostMapping("/mint/{chain}/{spuId}")
    @AutoLog(operateType = LogOperateTypeEnum.MINT, logType = LogTypeEnum.OPERATE)
    public Result mintGoods(
            @ApiParam("链名称") @PathVariable String chain,
            @ApiParam("商品ID") @PathVariable Long spuId) {
        boolean result = pmsBsnService.mintGoods(chain, spuId);
        return Result.judge(result);
    }

    @ApiOperation(value = "查询更新NFT商品")
    @GetMapping("/query/{chain}/{spuId}")
    public Result updateMintGoods(
            @ApiParam("链名称") @PathVariable String chain,
            @ApiParam("商品ID") @PathVariable Long spuId) {
        boolean result = pmsBsnService.queryGoods(chain, spuId);
        return Result.judge(result);
    }

}
