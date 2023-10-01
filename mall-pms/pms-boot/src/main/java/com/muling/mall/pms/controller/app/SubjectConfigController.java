package com.muling.mall.pms.controller.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.muling.common.enums.VisibleEnum;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.mall.pms.pojo.entity.PmsSubjectConfig;
import com.muling.mall.pms.pojo.vo.GoodsPageVO;
import com.muling.mall.pms.pojo.vo.SubjectVO;
import com.muling.mall.pms.service.IPmsSpuService;
import com.muling.mall.pms.service.IPmsSubjectConfigService;
import com.muling.mall.pms.service.IPmsSubjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@Api(tags = "app-系列配置")
@RestController
@RequestMapping("/app-api/v1/subject/config")
@RequiredArgsConstructor
public class SubjectConfigController {

    private final IPmsSubjectConfigService subjectConfigService;

    private final IPmsSubjectService subjectService;

    private final IPmsSpuService spuService;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "Subject ID") Long subjectId,
            @ApiParam(value = "Spu ID") Long spuId
    ) {
        LambdaQueryWrapper<PmsSubjectConfig> queryWrapper = new LambdaQueryWrapper<PmsSubjectConfig>()
                .eq(subjectId != null, PmsSubjectConfig::getSubjectId, subjectId)
                .eq(spuId != null, PmsSubjectConfig::getSpuId, spuId)
                .eq(PmsSubjectConfig::getVisible, VisibleEnum.DISPLAY)
                .orderByDesc(PmsSubjectConfig::getUpdated)
                .orderByDesc(PmsSubjectConfig::getCreated);
        Page<PmsSubjectConfig> result = subjectConfigService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "系列配置详情列表分页")
    @GetMapping("/spu")
    public Result pageSku(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "Spu ID") Long spuId
    ) {
        LambdaQueryWrapper<PmsSubjectConfig> queryWrapper = new LambdaQueryWrapper<PmsSubjectConfig>()
                .eq(spuId != null, PmsSubjectConfig::getSpuId, spuId)
                .eq(PmsSubjectConfig::getVisible, VisibleEnum.DISPLAY)
                .orderByDesc(PmsSubjectConfig::getUpdated)
                .orderByDesc(PmsSubjectConfig::getCreated);
        Page<PmsSubjectConfig> result = subjectConfigService.page(new Page<>(pageNum, pageSize), queryWrapper);
        List<Long> subjectIds = Lists.newArrayList();
        if (subjectIds.isEmpty()) {
            return Result.failed("系列为空");
        }
        result.getRecords().forEach(subjectConfig -> {
            subjectIds.add(subjectConfig.getSubjectId());
        });
        List<SubjectVO> subjectVOList = subjectService.getAppSubjectDetails(subjectIds);
        return Result.success(subjectVOList);
    }

    @ApiOperation(value = "系列配置商品列表分页")
    @GetMapping("/subject")
    public Result pageSpu(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "Subject ID") Long subjectId
    ) {
        LambdaQueryWrapper<PmsSubjectConfig> queryWrapper = new LambdaQueryWrapper<PmsSubjectConfig>()
                .eq(subjectId != null, PmsSubjectConfig::getSubjectId, subjectId)
                .eq(PmsSubjectConfig::getVisible, VisibleEnum.DISPLAY)
                .orderByDesc(PmsSubjectConfig::getUpdated)
                .orderByDesc(PmsSubjectConfig::getCreated);
        Page<PmsSubjectConfig> result = subjectConfigService.page(new Page<>(pageNum, pageSize), queryWrapper);
        List<Long> spuIds = Lists.newArrayList();
        result.getRecords().forEach(spu -> {
            spuIds.add(spu.getSpuId());
        });
        List<GoodsPageVO> goodsPageVOList = spuService.getAppSpuDetails(spuIds);
        return Result.success(goodsPageVOList);
    }

}
