package com.muling.mall.pms.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.annotation.AutoLog;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.mall.pms.common.enums.ViewTypeEnum;
import com.muling.mall.pms.pojo.entity.PmsGround;
import com.muling.mall.pms.pojo.form.GroundForm;
import com.muling.mall.pms.service.IPmsGroundService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Api(tags = "admin-广场管理")
@RestController("GroundController")
@RequestMapping("/api/v1/ground")
@RequiredArgsConstructor
public class GroundController {

    private final IPmsGroundService groundService;

    @ApiOperation(value = "广场列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "类型") Integer type
    ) {
        LambdaQueryWrapper<PmsGround> queryWrapper = new LambdaQueryWrapper<PmsGround>()
                .eq(type != null, PmsGround::getType, type)
                .orderByDesc(PmsGround::getSort)
                .orderByDesc(PmsGround::getUpdated);
        Page<PmsGround> result = groundService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "新增广场产品")
    @PostMapping
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    public Result add(
            @RequestBody GroundForm groundForm) {
        boolean status = groundService.save(groundForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "修改广场产品")
    @PutMapping(value = "/{id}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result updateById(
            @ApiParam("热度id") @PathVariable Long id,
            @RequestBody GroundForm groundForm) {
        boolean status = groundService.updateById(id, groundForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "显示")
    @PutMapping("/display")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result display(
            @ApiParam("ground ids") @RequestParam(required = true) List<Long> groundIds) {
        boolean status = groundService.update(new LambdaUpdateWrapper<PmsGround>()
                .in(PmsGround::getId, groundIds)
                .eq(PmsGround::getVisible, ViewTypeEnum.INVISIBLE)
                .set(PmsGround::getVisible, ViewTypeEnum.VISIBLE));
        return Result.judge(status);
    }

    @ApiOperation(value = "隐藏")
    @PutMapping("/hide")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result hide(
            @ApiParam("ground ids") @RequestParam(required = true) List<Long> groundIds) {
        boolean status = groundService.update(new LambdaUpdateWrapper<PmsGround>()
                .in(PmsGround::getId, groundIds)
                .eq(PmsGround::getVisible, ViewTypeEnum.VISIBLE)
                .set(PmsGround::getVisible, ViewTypeEnum.INVISIBLE));
        return Result.judge(status);
    }

    @ApiOperation(value = "删除广场")
    @DeleteMapping("/{ids}")
    @AutoLog(operateType = LogOperateTypeEnum.DELETE, logType = LogTypeEnum.OPERATE)
    public Result delete(
            @ApiParam("id集合") @PathVariable("ids") String ids) {
        boolean status = groundService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.judge(status);
    }
}
