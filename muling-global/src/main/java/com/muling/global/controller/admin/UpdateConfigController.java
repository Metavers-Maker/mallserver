package com.muling.global.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.global.pojo.entity.UpdateConfig;
import com.muling.global.pojo.form.UpdateConfigForm;
import com.muling.global.service.IUpdateConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Api(tags = "admin-更新配置")
@RestController("UpdateConfigController")
@RequestMapping("/api/v1/update-config")
@RequiredArgsConstructor
public class UpdateConfigController {

    private final IUpdateConfigService updateConfigService;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam("名称") String name
    ) {
        LambdaQueryWrapper<UpdateConfig> queryWrapper = new LambdaQueryWrapper<UpdateConfig>()
                .like(StrUtil.isNotBlank(name), UpdateConfig::getName, name)
                .orderByDesc(UpdateConfig::getUpdated)
                .orderByDesc(UpdateConfig::getCreated);
        Page<UpdateConfig> result = updateConfigService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "新增")
    @PostMapping
    public Result add(
            @RequestBody @Validated UpdateConfigForm updateConfigForm
    ) {
        boolean result = updateConfigService.add(updateConfigForm);
        return Result.judge(result);
    }

    @ApiOperation(value = "修改")
    @PutMapping("/{updateConfigId}")
    public Result update(
            @ApiParam(value = "ID") @PathVariable Long updateConfigId,
            @RequestBody @Validated UpdateConfigForm updateConfigForm
    ) {
        boolean result = updateConfigService.update(updateConfigId, updateConfigForm);
        return Result.judge(result);
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("/{ids}")
    public Result deleteAddress(
            @ApiParam("地址ID，过个以英文逗号(,)分割") @PathVariable String ids
    ) {
        boolean status = updateConfigService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.judge(status);
    }
}
