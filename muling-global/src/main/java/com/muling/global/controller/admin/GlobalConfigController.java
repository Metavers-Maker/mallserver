package com.muling.global.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.global.pojo.entity.GlobalConfig;
import com.muling.global.pojo.form.GlobalConfigForm;
import com.muling.global.service.IGlobalConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Api(tags = "admin-全局配置")
@RestController("GlobalConfigController")
@RequestMapping("/api/v1/global-config")
@RequiredArgsConstructor
public class GlobalConfigController {

    private final IGlobalConfigService globalConfigService;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam("名称") String name,
            @ApiParam("类型") String type
    ) {
        LambdaQueryWrapper<GlobalConfig> queryWrapper = new LambdaQueryWrapper<GlobalConfig>()
                .eq(type!=null,GlobalConfig::getType,type)
                .like(StrUtil.isNotBlank(name), GlobalConfig::getName, name)
                .orderByDesc(GlobalConfig::getUpdated)
                .orderByDesc(GlobalConfig::getCreated);
        Page<GlobalConfig> result = globalConfigService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "新增")
    @PostMapping
    public Result add(
            @RequestBody @Validated GlobalConfigForm globalConfigForm
    ) {
        boolean result = globalConfigService.add(globalConfigForm);
        return Result.judge(result);
    }

    @ApiOperation(value = "修改")
    @PutMapping("/{globalConfigId}")
    public Result update(
            @ApiParam(value = "ID") @PathVariable Long globalConfigId,
            @RequestBody @Validated GlobalConfigForm globalConfigForm
    ) {
        boolean result = globalConfigService.update(globalConfigId, globalConfigForm);
        return Result.judge(result);
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("/{ids}")
    public Result deleteAddress(
            @ApiParam("地址ID，过个以英文逗号(,)分割") @PathVariable String ids
    ) {
        boolean status = globalConfigService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.judge(status);
    }
}
