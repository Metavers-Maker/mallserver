package com.muling.mall.pms.controller.app;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.mall.pms.common.enums.StatusEnum;
import com.muling.mall.pms.common.enums.ViewTypeEnum;
import com.muling.mall.pms.es.service.PmsSpuEService;
import com.muling.mall.pms.pojo.entity.PmsBanner;
import com.muling.mall.pms.pojo.entity.PmsSpu;
import com.muling.mall.pms.pojo.query.SpuAdminPageQuery;
import com.muling.mall.pms.pojo.query.SpuPageQuery;
import com.muling.mall.pms.pojo.vo.GoodsPageVO;
import com.muling.mall.pms.service.IPmsSpuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "app-商品信息")
@RestController
@RequestMapping("/app-api/v1/spu")
@RequiredArgsConstructor
public class SpuController {

    private final IPmsSpuService spuService;

    @ApiOperation(value = "分页列表")
    @GetMapping("/list/page")
    public Result<List<GoodsPageVO>> list(SpuPageQuery spuPageQuery) {
        List<GoodsPageVO> result = spuService.pageSpuDetails(spuPageQuery);
        return Result.success(result);
    }

    @ApiOperation(value = "获取商品详情")
    @GetMapping("/{spuId}")
    public Result<GoodsPageVO> getBySpuId(
            @ApiParam("商品ID") @PathVariable Long spuId) {
        GoodsPageVO goodsDetailVO = spuService.getAppSpuDetail(spuId);
        return Result.success(goodsDetailVO);
    }

    @ApiOperation(value = "商品详情列表")
    @GetMapping
    public Result<List<GoodsPageVO>> list(
            @ApiParam("商品ID数组") @RequestParam(required = true) List<Long> spuIds) {
        if (spuIds == null || spuIds.isEmpty() || spuIds.size() > 20) {
            return Result.failed("商品ID不能为空，且不能超过20个");
        }
        List<GoodsPageVO> result = spuService.getAppSpuDetails(spuIds);
        return Result.success(result);
    }

}
