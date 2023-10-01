
package com.muling.mall.wms.controller.app;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.common.web.annotation.RequestLimit;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.wms.common.enums.MarketStatusEnum;
import com.muling.mall.wms.pojo.entity.WmsMarket;
import com.muling.mall.wms.pojo.form.app.MarketCreateForm;
import com.muling.mall.wms.pojo.form.app.MarketUpdateForm;
import com.muling.mall.wms.pojo.query.app.MarketBuyPageQueryApp;
import com.muling.mall.wms.pojo.query.app.MarketPageQueryApp;
import com.muling.mall.wms.pojo.query.app.MarketSellPageQueryApp;
import com.muling.mall.wms.pojo.vo.MarketVO;
import com.muling.mall.wms.service.IMarketService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "app-市场管理")
@RestController
@RequestMapping("/app-api/v1/market")
@RequiredArgsConstructor
public class MarketController {


    private final IMarketService marketService;

    @ApiOperation(value = "列表")
    @GetMapping("/page")
    public PageResult<MarketVO> list(MarketPageQueryApp queryParams) {
        IPage<MarketVO> page = marketService.page(queryParams);
        return PageResult.success(page);
    }

    @ApiOperation(value = "用户相关市场列表（买卖）")
    @GetMapping("/page/me")
    @RequestLimit(limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public PageResult<MarketVO> pageMe(MarketPageQueryApp queryParams) {
        IPage<MarketVO> page = marketService.pageMe(queryParams);
        return PageResult.success(page);
    }

    @ApiOperation(value = "锁定订单市场列表")
    @GetMapping("/buy/page/me")
    @RequestLimit(count = 10, time = 60, waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public PageResult<MarketVO> buyPageMe(MarketBuyPageQueryApp queryParams) {
        IPage<MarketVO> page = marketService.buyPageMe(queryParams);
        return PageResult.success(page);
    }

    @ApiOperation(value = "发起订单市场列表")
    @GetMapping("/sell/page/me")
    @RequestLimit(count = 10, time = 60, waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public PageResult<MarketVO> sellPageMe(MarketSellPageQueryApp queryParams) {
        IPage<MarketVO> page = marketService.sellPageMe(queryParams);
        return PageResult.success(page);
    }

    @ApiOperation(value = "更新")
    @PutMapping(value = "/{marketId}")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public <T> Result<T> update(
            @PathVariable Long marketId,
            @Valid @RequestBody MarketUpdateForm marketForm) {
        boolean status = marketService.updateById(marketId, marketForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "上架")
    @PutMapping("/up")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result up(
            @ApiParam("marketIds") @RequestParam(required = true) List<Long> marketIds) {
        Long memberId = MemberUtils.getMemberId();
        boolean status = marketService.update(new LambdaUpdateWrapper<WmsMarket>()
                .eq(WmsMarket::getMemberId, memberId)
                .in(WmsMarket::getId, marketIds)
                .eq(WmsMarket::getStatus, MarketStatusEnum.DOWN)
                .set(WmsMarket::getStatus, MarketStatusEnum.UP));
        return Result.judge(status);
    }

    @ApiOperation(value = "下架")
    @PutMapping("/down")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result down(
            @ApiParam("marketIds") @RequestParam(required = true) List<Long> marketIds) {
        Long memberId = MemberUtils.getMemberId();
        boolean status = marketService.update(new LambdaUpdateWrapper<WmsMarket>()
                .eq(WmsMarket::getMemberId, memberId)
                .in(WmsMarket::getId, marketIds)
                .eq(WmsMarket::getStatus, MarketStatusEnum.UP)
                .set(WmsMarket::getStatus, MarketStatusEnum.DOWN));
        return Result.judge(status);
    }


    @ApiOperation(value = "求购")
    @PostMapping("/create/buy")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result createBuy(@Valid @RequestBody MarketCreateForm marketCreateForm) {
        boolean f = marketService.createBuy(marketCreateForm);
        return Result.judge(f);
    }

    @ApiOperation(value = "获得求购星数")
    @GetMapping("/star/buy")
    public Result<Integer> buyStar() {
        Integer times = marketService.buyStar();
        return Result.success(times);
    }

    @ApiOperation(value = "销毁求购")
    @PostMapping("/destroy/buy")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result destroyBuy(@RequestParam(required = true) Long marketId) {
        boolean f = marketService.destroyBuy(marketId);
        return Result.judge(f);
    }

    @ApiOperation(value = "关闭")
    @PostMapping("/close")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result close(
            @ApiParam("marketIds") @RequestParam(required = true) List<Long> marketIds) {
        boolean status = marketService.close(marketIds);
        return Result.judge(status);
    }

    @ApiOperation(value = "卖家订单锁定")
    @PostMapping("/lock")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result lock(
            @RequestParam(required = true) Long marketId) {
        boolean f = marketService.lock(marketId);
        return Result.judge(f);
    }

    @ApiOperation(value = "卖家取消")
    @PostMapping("/buy/cancle")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result buyCancle(
            @RequestParam(required = true) Long marketId) {
        boolean f = marketService.buyCancel(marketId);
        return Result.judge(f);
    }

    @ApiOperation(value = "买家取消")
    @PostMapping("/cancel")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result cancel(
            @RequestParam(required = true) Long marketId) {
        boolean f = marketService.cancel(marketId);
        return Result.judge(f);
    }

    @ApiOperation(value = "买家提交")
    @PostMapping("/commit")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result commit(
            @RequestParam(required = true) Long marketId) {
        boolean f = marketService.commit(marketId);
        return Result.judge(f);
    }

    @ApiOperation(value = "卖家确认")
    @PostMapping("/confirm")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result confirm(
            @RequestParam(required = true) Long marketId) {
        boolean f = marketService.confirm(marketId);
        return Result.judge(f);
    }

}
