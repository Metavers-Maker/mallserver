package com.muling.mall.wms.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.common.web.annotation.RequestLimit;
import com.muling.mall.wms.common.enums.MarketStatusEnum;
import com.muling.mall.wms.common.enums.MarketStepEnum;
import com.muling.mall.wms.pojo.entity.WmsMarket;
import com.muling.mall.wms.pojo.entity.WmsWalletLog;
import com.muling.mall.wms.pojo.vo.MarketDispatchVO;
import com.muling.mall.wms.service.IMarketService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Api(tags = "admin-市场")
@RestController("MarketController")
@RequestMapping("/api/v1/market")
@RequiredArgsConstructor
public class MarketController {

    private final IMarketService marketService;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "订单状态") Integer status,
            @ApiParam(value = "进度") Integer step,
            @ApiParam(value = "分红") Integer dispatch,
            @ApiParam(value = "订单拥有者") Long memberId,
            @ApiParam(value = "订单匹配者") Long buyerId,
            @ApiParam(value = "订单号") Long orderSn,
            @ApiParam(value = "开始时间") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime started,
            @ApiParam(value = "结束时间") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime ended,
            @ApiParam(value = "排序") String orderBy,
            @ApiParam(value = "排序方式") boolean isAsc
    ) {

        QueryWrapper<WmsMarket> queryWrapper = new QueryWrapper<WmsMarket>()
                .eq(status != null, "status", status)
                .eq(step != null, "step", step)
                .eq(dispatch != null, "dispatch", dispatch)
                .eq(memberId != null, "member_id", memberId)
                .eq(orderSn != null, "order_sn", orderSn)
                .eq(buyerId != null, "buyer_id", buyerId)
                .ge(started != null, "created", started)
                .le(ended != null, "created", ended)
                .orderBy(StrUtil.isNotBlank(orderBy), isAsc, orderBy)
                .orderByDesc("created");
        Page<WmsMarket> result = marketService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    //
    @ApiOperation(value = "订单冻结")
    @PutMapping("/coin/freeze")
    public Result coinFreeze(@RequestParam(required = true) Long marketId) {
        boolean f = marketService.coinFreeze(marketId);
        return Result.judge(f);
    }

    @ApiOperation(value = "积分退还")
    @PutMapping("/coin/return")
    public Result coinReturn(@RequestParam(required = true) Long marketId) {
        boolean f = marketService.coinReturn(marketId);
        return Result.judge(f);
    }

    @ApiOperation(value = "积分支付")
    @PutMapping("/coin/pay")
    public Result coinPay(@RequestParam(required = true) Long marketId) {
        boolean f = marketService.coinPay(marketId);
        return Result.judge(f);
    }

    @ApiOperation(value = "订单撤销")
    @PutMapping("/coin/cancle")
    public Result coinCancle(@RequestParam(required = true) Long marketId) {
        boolean f = marketService.coinCancle(marketId);
        return Result.judge(f);
    }

    @ApiOperation(value = "分红")
    @PutMapping("/coin/dispatch")
    public Result coinDispatch(
            @ApiParam(value = "单日", example = "0") Integer single,
            @ApiParam(value = "时间") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime date) {
        MarketDispatchVO marketDispatchVO = marketService.coinDispatch(date);
        return Result.success(marketDispatchVO);
    }

}
