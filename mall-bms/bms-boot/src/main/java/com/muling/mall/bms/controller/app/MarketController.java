
package com.muling.mall.bms.controller.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.common.web.annotation.RequestLimit;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.bms.converter.MarketConverter;
import com.muling.mall.bms.enums.MarketStatusEnum;
import com.muling.mall.bms.pojo.entity.OmsMarket;
import com.muling.mall.bms.pojo.form.app.MarketCreateForm;
import com.muling.mall.bms.pojo.form.app.MarketUpdateForm;
import com.muling.mall.bms.pojo.query.app.MarketBuyPageQueryApp;
import com.muling.mall.bms.pojo.query.app.MarketPageQueryApp;
import com.muling.mall.bms.pojo.vo.app.MarketVO;
import com.muling.mall.bms.service.IMarketService;
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

    @ApiOperation(value = "根据市场ID获取详情")
    @GetMapping("/list")
//    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result listByIds(
            @ApiParam("ids") @RequestParam(required = true) List<Long> ids) {
        List<MarketVO> marketVOList = marketService.getListByIds(ids);
        return Result.success(marketVOList);
    }

    @ApiOperation(value = "用户市场列表")
    @GetMapping("/page/me")
//    @RequestLimit(waits = 2, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public PageResult<MarketVO> pageMe(MarketPageQueryApp queryParams) {
        IPage<MarketVO> page = marketService.pageMe(queryParams);
        return PageResult.success(page);
    }

    @ApiOperation(value = "用户购买市场列表")
    @GetMapping("/buy/page/me")
    @RequestLimit(waits = 2, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public PageResult<MarketVO> buyPageMe(MarketBuyPageQueryApp queryParams) {
        IPage<MarketVO> page = marketService.buyPageMe(queryParams);
        return PageResult.success(page);
    }

    @ApiOperation(value = "创建")
    @PostMapping
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result add(@Valid @RequestBody MarketCreateForm marketCreateForm) {
        boolean f = marketService.save(marketCreateForm);
        return Result.judge(f);
    }

    @ApiOperation(value = "关闭")
    @PutMapping("/close")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result close(
            @ApiParam("itemIds") @RequestParam(required = true) List<Long> itemIds) {
        boolean status = marketService.close(itemIds);
        return Result.judge(status);
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

//    @ApiOperation(value = "上架")
//    @PutMapping("/up")
//    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
//    public Result up(
//            @ApiParam("itemIds") @RequestParam(required = true) List<Long> itemIds) {
//        Long memberId = MemberUtils.getMemberId();
//        boolean status = marketService.update(new LambdaUpdateWrapper<OmsMarket>()
//                .eq(OmsMarket::getMemberId, memberId)
//                .in(OmsMarket::getItemId, itemIds)
//                .eq(OmsMarket::getStatus, MarketStatusEnum.DOWN)
//                .set(OmsMarket::getStatus, MarketStatusEnum.UP));
//        return Result.judge(status);
//    }
//
//    @ApiOperation(value = "下架")
//    @PutMapping("/down")
//    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
//    public Result down(
//            @ApiParam("itemIds") @RequestParam(required = true) List<Long> itemIds) {
//        Long memberId = MemberUtils.getMemberId();
//        boolean status = marketService.update(new LambdaUpdateWrapper<OmsMarket>()
//                .eq(OmsMarket::getMemberId, memberId)
//                .in(OmsMarket::getItemId, itemIds)
//                .eq(OmsMarket::getStatus, MarketStatusEnum.UP)
//                .set(OmsMarket::getStatus, MarketStatusEnum.DOWN));
//        return Result.judge(status);
//    }
//    @ApiOperation(value = "购买")
//    @PutMapping("/buy")
//    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
//    public Result buy(
//            @RequestParam(required = true) Long marketId) {
//        boolean f = marketService.buy(marketId);
//
//        return Result.judge(f);
//    }

}
