package com.muling.mall.bms.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.result.PageResult;
import com.muling.mall.bms.pojo.entity.OmsExchangeLog;
import com.muling.mall.bms.service.IExchangeLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "admin-兑换日志")
@RestController("ExchangeLogController")
@RequestMapping("/api/v1/exchange-log")
@RequiredArgsConstructor
public class ExchangeLogController {

    private final IExchangeLogService exchangeLogService;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "用户ID") Long memberId,
            @ApiParam(value = "兑换类型") Long exchangeType,
            @ApiParam(value = "spuId") Long spuId,
            @ApiParam(value = "名称") String itemName
    ) {
        LambdaQueryWrapper<OmsExchangeLog> queryWrapper = new LambdaQueryWrapper<OmsExchangeLog>()
                .eq(memberId != null, OmsExchangeLog::getMemberId, memberId)
                .eq(exchangeType != null, OmsExchangeLog::getExchangeType, exchangeType)
                .eq(spuId != null, OmsExchangeLog::getSpuId, spuId)
                .like(StrUtil.isNotBlank(itemName), OmsExchangeLog::getItemName, itemName)
                .orderByDesc(OmsExchangeLog::getUpdated)
                .orderByDesc(OmsExchangeLog::getCreated);
        Page<OmsExchangeLog> result = exchangeLogService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }


}
