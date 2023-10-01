package com.muling.mall.task.controller.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.common.web.annotation.RequestLimit;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.task.converter.TaskMemberConverter;
import com.muling.mall.task.pojo.entity.TaskConfig;
import com.muling.mall.task.pojo.entity.TaskMember;
import com.muling.mall.task.pojo.vo.app.TaskItemRewardVO;
import com.muling.mall.task.pojo.vo.app.TaskMemberVO;
import com.muling.mall.task.pojo.vo.app.TaskRewardVO;
import com.muling.mall.task.service.ITaskMemberService;
import com.muling.mall.task.service.ITaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "app-任务管理")
@RestController
@RequestMapping("/app-api/v1/task")
@RequiredArgsConstructor
public class TaskController {

    private final ITaskService taskService;

    private final ITaskMemberService taskMemberService;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize

    ) {
        Long memberId = MemberUtils.getMemberId();
        LambdaQueryWrapper<TaskMember> queryWrapper = new LambdaQueryWrapper<TaskMember>()
                .eq(TaskMember::getMemberId, memberId)
                .orderByDesc(TaskMember::getUpdated);
        Page<TaskMember> page = taskMemberService.page(new Page<>(pageNum, pageSize), queryWrapper);
        Page<TaskMemberVO> result = TaskMemberConverter.INSTANCE.entity2PageVO(page);
        return PageResult.success(result);
    }

    //end class
}
