package com.muling.mall.bms.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.annotation.AutoLog;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.mall.bms.enums.StatusEnum;
import com.muling.mall.bms.pojo.entity.OmsMarketConfig;
import com.muling.mall.bms.pojo.form.admin.MarketConfigForm;
import com.muling.mall.bms.service.IMarketConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Api(tags = "admin-市场配置")
@RestController("MarketConfigController")
@RequestMapping("/api/v1/market-config")
@RequiredArgsConstructor
public class MarketConfigController {

    private final IMarketConfigService marketConfigService;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "名称") String name,
            @ApiParam(value = "spuId") Long spuId
    ) {

        LambdaQueryWrapper<OmsMarketConfig> queryWrapper = new LambdaQueryWrapper<OmsMarketConfig>()
                .like(StrUtil.isNotBlank(name), OmsMarketConfig::getName, name)
                .eq(spuId != null, OmsMarketConfig::getSpuId, spuId)
                .orderByDesc(OmsMarketConfig::getUpdated);
        Page<OmsMarketConfig> result = marketConfigService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "市场配置列表")
    @GetMapping("/list")
    public Result<List<OmsMarketConfig>> list(
            @ApiParam("商品ID数组") @RequestParam(required = true) List<Long> spuIds) {
        if (spuIds == null || spuIds.isEmpty() || spuIds.size() > 20) {
            return Result.failed("商品ID不能为空，且不能超过20个");
        }
        List<OmsMarketConfig> result = marketConfigService.list(new LambdaQueryWrapper<OmsMarketConfig>()
                .in(OmsMarketConfig::getSpuId, spuIds));
        return Result.success(result);
    }

    @ApiOperation(value = "新增")
    @PostMapping
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    public Result add(
            @RequestBody MarketConfigForm configForm) {
        boolean status = marketConfigService.save(configForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "修改")
    @PutMapping(value = "/{id}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result updateById(
            @ApiParam("id") @PathVariable Long id,
            @RequestBody MarketConfigForm configForm) {
        boolean status = marketConfigService.updateById(id, configForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "可用")
    @PutMapping("/enable")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result display(
            @ApiParam("ids") @RequestParam(required = true) List<Long> ids) {
        boolean status = marketConfigService.update(new LambdaUpdateWrapper<OmsMarketConfig>()
                .in(OmsMarketConfig::getId, ids)
                .eq(OmsMarketConfig::getStatus, StatusEnum.DISABLED)
                .set(OmsMarketConfig::getStatus, StatusEnum.ENABLE));
        return Result.judge(status);
    }

    @ApiOperation(value = "不可用")
    @PutMapping("/disabled")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result hide(
            @ApiParam("ids") @RequestParam(required = true) List<Long> ids) {
        boolean status = marketConfigService.update(new LambdaUpdateWrapper<OmsMarketConfig>()
                .in(OmsMarketConfig::getId, ids)
                .eq(OmsMarketConfig::getStatus, StatusEnum.ENABLE)
                .set(OmsMarketConfig::getStatus, StatusEnum.DISABLED));
        return Result.judge(status);
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("/{ids}")
    @AutoLog(operateType = LogOperateTypeEnum.DELETE, logType = LogTypeEnum.OPERATE)
    public Result delete(
            @ApiParam("id集合") @PathVariable("ids") String ids) {
        boolean status = marketConfigService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.judge(status);
    }
}
