package com.muling.admin.controller;

import com.muling.admin.pojo.dto.StatisticsDTO;
import com.muling.admin.service.IStatisticsService;
import com.muling.common.annotation.AutoLog;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.protocol.StatisticsRequest;
import com.muling.common.result.Result;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author chen
 */
@Api(tags = "admin-统计和报表")
@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final IStatisticsService statisticsService;

    @GetMapping("/calculate")
    @ApiOperation(value = "模拟测试接口")
    public Result calculate() {

        statisticsService.calculateFix();
        return Result.success();
    }

    @ApiOperation(value = "查询统计数据")
    @PostMapping("/query")
    @AutoLog(operateType = LogOperateTypeEnum.LIST, logType = LogTypeEnum.OPERATE)
    public Result<StatisticsDTO> query(@ApiParam("统计数据查询对象") @RequestBody StatisticsRequest statisticsRequest) {
        return Result.success(statisticsService.query(statisticsRequest));
    }

}
