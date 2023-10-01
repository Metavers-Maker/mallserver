package com.muling.mall.pms.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.annotation.AutoLog;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.mall.pms.common.enums.LinkTypeEnum;
import com.muling.mall.pms.common.enums.ViewTypeEnum;
import com.muling.mall.pms.pojo.entity.PmsBanner;
import com.muling.mall.pms.pojo.form.BannerForm;
import com.muling.mall.pms.service.IPmsBannerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Api(tags = "admin-轮播管理")
@RestController("BannerController")
@RequestMapping("/api/v1/banners")
@RequiredArgsConstructor
public class BannerController {

    private final IPmsBannerService bannerService;

    @ApiOperation(value = "轮播列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "轮播名称") String name,
            @ApiParam(value = "类型", allowMultiple = true) LinkTypeEnum[] linkTypes) {

        LambdaQueryWrapper<PmsBanner> queryWrapper = new LambdaQueryWrapper<PmsBanner>()
                .like(StrUtil.isNotBlank(name), PmsBanner::getName, name)
                .in(linkTypes != null && linkTypes.length > 0, PmsBanner::getLinkType, linkTypes)
                .orderByDesc(PmsBanner::getSort)
                .orderByDesc(PmsBanner::getUpdated);
        Page<PmsBanner> result = bannerService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "轮播详情")
    @GetMapping("/{id}")
    public Result getById(@ApiParam("轮播id") @PathVariable Integer id) {
        PmsBanner brand = bannerService.getById(id);
        return Result.success(brand);
    }

    @ApiOperation(value = "新增轮播")
    @PostMapping
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    public Result add(
            @RequestBody BannerForm bannerForm) {
        boolean status = bannerService.save(bannerForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "修改轮播")
    @PutMapping(value = "/{id}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result updateById(
            @ApiParam("轮播id") @PathVariable Long id,
            @RequestBody BannerForm bannerForm) {

        boolean status = bannerService.updateById(id, bannerForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "显示")
    @PutMapping("/display")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result display(
            @ApiParam("banner ids") @RequestParam(required = true) List<Long> bannerIds) {
        boolean status = bannerService.update(new LambdaUpdateWrapper<PmsBanner>()
                .in(PmsBanner::getId, bannerIds)
                .eq(PmsBanner::getVisible, ViewTypeEnum.INVISIBLE)
                .set(PmsBanner::getVisible, ViewTypeEnum.VISIBLE));
        return Result.judge(status);
    }

    @ApiOperation(value = "隐藏")
    @PutMapping("/hide")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result hide(
            @ApiParam("banner ids") @RequestParam(required = true) List<Long> bannerIds) {
        boolean status = bannerService.update(new LambdaUpdateWrapper<PmsBanner>()
                .in(PmsBanner::getId, bannerIds)
                .eq(PmsBanner::getVisible, ViewTypeEnum.VISIBLE)
                .set(PmsBanner::getVisible, ViewTypeEnum.INVISIBLE));
        return Result.judge(status);
    }

    @ApiOperation(value = "删除轮播")
    @DeleteMapping("/{ids}")
    @AutoLog(operateType = LogOperateTypeEnum.DELETE, logType = LogTypeEnum.OPERATE)
    public Result delete(
            @ApiParam("id集合") @PathVariable("ids") String ids) {
        boolean status = bannerService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.judge(status);
    }
}
