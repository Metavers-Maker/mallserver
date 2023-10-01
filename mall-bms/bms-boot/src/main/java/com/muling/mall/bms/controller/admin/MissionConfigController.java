package com.muling.mall.bms.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.mall.bms.pojo.entity.OmsMissionConfig;
import com.muling.mall.bms.pojo.form.admin.MissionConfigForm;
import com.muling.mall.bms.service.IMissionConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

//@Api(tags = "admin-任务")
@RestController("MissionConfigController")
@RequestMapping("/api/v1/mission-config")
@RequiredArgsConstructor
public class MissionConfigController {

    private final IMissionConfigService missionConfigService;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize
    ) {
        LambdaQueryWrapper<OmsMissionConfig> queryWrapper = new LambdaQueryWrapper<OmsMissionConfig>()
                .orderByDesc(OmsMissionConfig::getUpdated);
        Page<OmsMissionConfig> result = missionConfigService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "新增")
    @PostMapping
    public Result add(
            @RequestBody @Validated MissionConfigForm form
    ) {
        boolean result = missionConfigService.add(form);
        return Result.judge(result);
    }

    @ApiOperation(value = "修改")
    @PutMapping("/{id}")
    public Result update(
            @ApiParam(value = "ID") @PathVariable Long id,
            @RequestBody @Validated MissionConfigForm form
    ) {
        boolean result = missionConfigService.update(id, form);
        return Result.judge(result);
    }

    @ApiOperation(value = "修改显示状态")
    @PatchMapping(value = "/{id}")
    public Result display(
            @PathVariable Long id, Integer visible) {
        boolean status = missionConfigService.update(id, visible);
        return Result.judge(status);
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("/{ids}")
    public Result deleteAddress(
            @ApiParam("IDS以英文逗号(,)分割") @PathVariable String ids
    ) {
        List<String> list = Arrays.asList(ids.split(","));
        boolean status = missionConfigService.delete(list);
        return Result.judge(status);
    }
}
