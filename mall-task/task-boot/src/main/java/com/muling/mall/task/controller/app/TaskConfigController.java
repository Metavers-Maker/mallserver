package com.muling.mall.task.controller.app;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.muling.common.result.PageResult;
import com.muling.mall.task.pojo.query.app.TaskConfigPageQuery;
import com.muling.mall.task.pojo.vo.app.TaskConfigVO;
import com.muling.mall.task.service.ITaskConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = "app-任务配置")
@RestController
@RequestMapping("/app-api/v1/task-config")
@RequiredArgsConstructor
public class TaskConfigController {

    private final ITaskConfigService taskConfigService;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public PageResult page(TaskConfigPageQuery pageQuery) {
        IPage<TaskConfigVO> page = taskConfigService.page(pageQuery);
        return PageResult.success(page);
    }
    //
}
