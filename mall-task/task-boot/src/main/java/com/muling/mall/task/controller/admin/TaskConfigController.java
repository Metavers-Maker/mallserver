package com.muling.mall.task.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.enums.VisibleEnum;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.mall.task.pojo.entity.TaskConfig;
import com.muling.mall.task.pojo.form.admin.TaskConfigForm;
import com.muling.mall.task.service.ITaskConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Api(tags = "admin-任务配置")
@RestController("AdminTaskConfigController")
@RequestMapping("/api/v1/task-config")
@RequiredArgsConstructor
public class TaskConfigController {

    private final ITaskConfigService taskConfigService;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "任务类型", example = "0") Integer taskType
    ) {
        LambdaQueryWrapper<TaskConfig> queryWrapper = new LambdaQueryWrapper<TaskConfig>()
                .eq(taskType!=null,TaskConfig::getTaskType,taskType)
                .orderByDesc(TaskConfig::getTaskType)
                .orderByDesc(TaskConfig::getUpdated);
        Page<TaskConfig> result = taskConfigService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "新增")
    @PostMapping
    public Result add(
            @RequestBody @Validated TaskConfigForm form
    ) {
        boolean result = taskConfigService.add(form);
        return Result.judge(result);
    }

    @ApiOperation(value = "修改")
    @PutMapping("/{id}")
    public Result update(
            @ApiParam(value = "ID") @PathVariable Long id,
            @RequestBody @Validated TaskConfigForm form
    ) {
        boolean result = taskConfigService.update(id, form);
        return Result.judge(result);
    }

    @ApiOperation(value = "修改显示状态")
    @PatchMapping(value = "/{id}")
    public Result display(
            @PathVariable Long id, VisibleEnum visible) {
        boolean status = taskConfigService.update(id, visible);
        return Result.judge(status);
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("/{ids}")
    public Result deleteAddress(
            @ApiParam("IDS以英文逗号(,)分割") @PathVariable String ids
    ) {
        List<String> list = Arrays.asList(ids.split(","));
        boolean status = taskConfigService.delete(list);
        return Result.judge(status);
    }
}
