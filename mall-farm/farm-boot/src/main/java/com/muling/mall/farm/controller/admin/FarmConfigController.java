package com.muling.mall.farm.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.enums.VisibleEnum;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.mall.farm.pojo.entity.FarmConfig;
import com.muling.mall.farm.pojo.form.admin.FarmConfigForm;
import com.muling.mall.farm.service.IFarmConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Api(tags = "admin-农场配置")
@RestController("AdminFarmConfigController")
@RequestMapping("/api/v1/farm-config")
@RequiredArgsConstructor
public class FarmConfigController {

    private final IFarmConfigService farmConfigService;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize
    ) {
        LambdaQueryWrapper<FarmConfig> queryWrapper = new LambdaQueryWrapper<FarmConfig>()
                .orderByDesc(FarmConfig::getUpdated);
        Page<FarmConfig> result = farmConfigService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "新增")
    @PostMapping
    public Result add(
            @RequestBody @Validated FarmConfigForm form
    ) {
        boolean result = farmConfigService.add(form);
        return Result.judge(result);
    }

    @ApiOperation(value = "修改")
    @PutMapping("/{id}")
    public Result update(
            @ApiParam(value = "ID") @PathVariable Long id,
            @RequestBody @Validated FarmConfigForm form
    ) {
        boolean result = farmConfigService.update(id, form);
        return Result.judge(result);
    }

    @ApiOperation(value = "修改显示状态")
    @PatchMapping(value = "/{id}")
    public Result display(
            @PathVariable Long id, VisibleEnum visible) {
        boolean status = farmConfigService.update(id, visible);
        return Result.judge(status);
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("/{ids}")
    public Result deleteAddress(
            @ApiParam("IDS以英文逗号(,)分割") @PathVariable String ids
    ) {
        List<String> list = Arrays.asList(ids.split(","));
        boolean status = farmConfigService.delete(list);
        return Result.judge(status);
    }
}
