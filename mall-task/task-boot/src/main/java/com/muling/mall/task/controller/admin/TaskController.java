package com.muling.mall.task.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.common.web.annotation.RequestLimit;
import com.muling.mall.task.pojo.entity.TaskMember;
import com.muling.mall.task.service.ITaskMemberService;
import com.muling.mall.task.service.ITaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = "admin-任务")
@RestController("AdminTaskController")
@RequestMapping("/api/v1/task")
@RequiredArgsConstructor
public class TaskController {

    private final ITaskMemberService taskMemberService;

    private final ITaskService taskService;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "状态", example = "0") Integer status
    ) {
        LambdaQueryWrapper<TaskMember> queryWrapper = new LambdaQueryWrapper<TaskMember>()
                .eq(status != null, TaskMember::getStatus, status)
                .orderByDesc(TaskMember::getUpdated);
        Page<TaskMember> result = taskMemberService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    @ApiOperation("审核")
    @PostMapping("/check/{taskMemberId}")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result check(@PathVariable Long taskMemberId, @RequestParam Integer status) {
        boolean result = taskService.check(taskMemberId, status);
        return Result.judge(result);
    }

}
