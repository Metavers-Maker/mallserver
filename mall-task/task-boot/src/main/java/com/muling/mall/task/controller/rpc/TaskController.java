package com.muling.mall.task.controller.rpc;

import com.muling.common.result.Result;
import com.muling.common.web.annotation.RequestLimit;
import com.muling.mall.task.service.ITaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = "rpc-任务管理")
@RestController("RpcTaskController")
@RequestMapping("/app-api/v1/rpc/task")
@RequiredArgsConstructor
public class TaskController {

    private final ITaskService taskService;

    @ApiOperation("领取")
    @PostMapping("/draw")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result draw(@RequestParam Long taskId) {

        boolean result = taskService.draw(taskId);
        return Result.judge(result);
    }

}
