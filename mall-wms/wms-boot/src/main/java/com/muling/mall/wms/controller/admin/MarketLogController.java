package com.muling.mall.wms.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.result.PageResult;
import com.muling.mall.wms.common.enums.MarketStatusEnum;
import com.muling.mall.wms.pojo.entity.WmsMarket;
import com.muling.mall.wms.pojo.entity.WmsMarketLog;
import com.muling.mall.wms.service.IMarketLogService;
import com.muling.mall.wms.service.IMarketService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Api(tags = "admin-市场")
@RestController("MarketLogController")
@RequestMapping("/api/v1/market/log")
@RequiredArgsConstructor
public class MarketLogController {

    private final IMarketLogService marketLogService;

    @ApiOperation(value = "日志列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "状态") Integer status,
            @ApiParam(value = "开始时间") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime started,
            @ApiParam(value = "结束时间") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime ended,
            @ApiParam(value = "排序") String orderBy,
            @ApiParam(value = "排序方式") boolean isAsc
    ) {

        QueryWrapper<WmsMarketLog> queryWrapper = new QueryWrapper<WmsMarketLog>()
                .eq(status != null, "status", status)
                .ge(started != null, "created", started)
                .le(ended != null, "created", ended)
                .orderBy(StrUtil.isNotBlank(orderBy), isAsc, orderBy)
                .orderByDesc("created");
        Page<WmsMarketLog> result = marketLogService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    //

}
