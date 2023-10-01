package com.muling.mall.wms.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.annotation.AutoLog;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.enums.StatusEnum;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.mall.wms.pojo.entity.WmsMarketConfig;
import com.muling.mall.wms.pojo.entity.WmsWallet;
import com.muling.mall.wms.pojo.entity.WmsWalletLog;
import com.muling.mall.wms.pojo.form.admin.MarketConfigForm;
import com.muling.mall.wms.service.IMarketConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
            @ApiParam(value = "开始时间") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime started,
            @ApiParam(value = "结束时间") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime ended
    ) {

        LambdaQueryWrapper<WmsMarketConfig> queryWrapper = new LambdaQueryWrapper<WmsMarketConfig>()
                .ge(started != null, WmsMarketConfig::getCreated, started)
                .le(ended != null, WmsMarketConfig::getCreated, ended)
                .orderByDesc(WmsMarketConfig::getUpdated);
        Page<WmsMarketConfig> result = marketConfigService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
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
        boolean status = marketConfigService.update(new LambdaUpdateWrapper<WmsMarketConfig>()
                .in(WmsMarketConfig::getId, ids)
                .eq(WmsMarketConfig::getStatus, StatusEnum.DISABLED)
                .set(WmsMarketConfig::getStatus, StatusEnum.ENABLE));
        return Result.judge(status);
    }

    @ApiOperation(value = "不可用")
    @PutMapping("/disabled")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result hide(
            @ApiParam("ids") @RequestParam(required = true) List<Long> ids) {
        boolean status = marketConfigService.update(new LambdaUpdateWrapper<WmsMarketConfig>()
                .in(WmsMarketConfig::getId, ids)
                .eq(WmsMarketConfig::getStatus, StatusEnum.ENABLE)
                .set(WmsMarketConfig::getStatus, StatusEnum.DISABLED));
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
