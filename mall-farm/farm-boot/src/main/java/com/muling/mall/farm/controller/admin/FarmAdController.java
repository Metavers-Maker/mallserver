package com.muling.mall.farm.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.result.PageResult;
import com.muling.mall.farm.pojo.dto.FarmDuomobItemDTO;
import com.muling.mall.farm.pojo.entity.*;
import com.muling.mall.farm.service.IFarmAdItemService;
import com.muling.mall.farm.service.IFarmAdService;
import com.muling.mall.farm.service.IFarmDuomobItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = "admin-农场Ad和Game")
@RestController("FarmAdController")
@RequestMapping("/api/v1/farm/ad")
@RequiredArgsConstructor
public class FarmAdController {

    private final IFarmAdService farmAdService;

    private final IFarmAdItemService farmAdItemService;

    private final IFarmDuomobItemService farmDuomobItemService;
    @ApiOperation(value = "视频任务列表分页")
    @GetMapping("/page")
    public PageResult adPage(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "会员ID") Long memberId
    ) {
        LambdaQueryWrapper<FarmAd> queryWrapper = new LambdaQueryWrapper<FarmAd>()
                .eq(memberId!=null,FarmAd::getMemberId,memberId)
                .orderByDesc(FarmAd::getUpdated);
        Page<FarmAd> result = farmAdService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "激励视频列表分页")
    @GetMapping("/item/page")
    public PageResult adItemPage(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "会员ID") Long memberId
    ) {
        LambdaQueryWrapper<FarmAdItem> queryWrapper = new LambdaQueryWrapper<FarmAdItem>()
                .eq(memberId!=null,FarmAdItem::getMemberId,memberId)
                .orderByDesc(FarmAdItem::getUpdated);
        Page<FarmAdItem> result = farmAdItemService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "游戏列表分页")
    @GetMapping("/game/page")
    public PageResult gamePage(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "会员ID") Long memberId
    ) {
        LambdaQueryWrapper<FarmDuomobItem> queryWrapper = new LambdaQueryWrapper<FarmDuomobItem>()
                .eq(memberId!=null,FarmDuomobItem::getMemberId,memberId)
                .orderByDesc(FarmDuomobItem::getCreated);
        Page<FarmDuomobItem> result = farmDuomobItemService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

}
