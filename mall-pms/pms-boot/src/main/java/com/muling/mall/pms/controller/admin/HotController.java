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
import com.muling.mall.pms.common.enums.ViewTypeEnum;
import com.muling.mall.pms.pojo.entity.PmsHot;
import com.muling.mall.pms.pojo.form.HotForm;
import com.muling.mall.pms.service.IPmsHotService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Api(tags = "admin-热度管理")
@RestController("HotController")
@RequestMapping("/api/v1/hots")
@RequiredArgsConstructor
public class HotController {

    private final IPmsHotService hotService;

    @ApiOperation(value = "热度列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam("热度名称") String name
    ) {
        LambdaQueryWrapper<PmsHot> queryWrapper = new LambdaQueryWrapper<PmsHot>()
                .like(StrUtil.isNotBlank(name), PmsHot::getName, name)
                .orderByDesc(PmsHot::getUpdated)
                .orderByDesc(PmsHot::getCreated);
        Page<PmsHot> result = hotService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "热度详情")
    @GetMapping("/{id}")
    public Result getById(@ApiParam("热度id") @PathVariable Integer id) {
        PmsHot brand = hotService.getById(id);
        return Result.success(brand);
    }

    @ApiOperation(value = "新增热度")
    @PostMapping
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    public Result add(
            @RequestBody HotForm hotForm) {
        boolean status = hotService.save(hotForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "修改热度")
    @PutMapping(value = "/{id}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result updateById(
            @ApiParam("热度id") @PathVariable Long id,
            @RequestBody HotForm hotForm) {
        boolean status = hotService.updateById(id, hotForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "显示")
    @PutMapping("/display")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result display(
            @ApiParam("hot ids") @RequestParam(required = true) List<Long> hotIds) {
        boolean status = hotService.update(new LambdaUpdateWrapper<PmsHot>()
                .in(PmsHot::getId, hotIds)
                .eq(PmsHot::getVisible, ViewTypeEnum.INVISIBLE)
                .set(PmsHot::getVisible, ViewTypeEnum.VISIBLE));
        return Result.judge(status);
    }

    @ApiOperation(value = "隐藏")
    @PutMapping("/hide")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result hide(
            @ApiParam("hot ids") @RequestParam(required = true) List<Long> hotIds) {
        boolean status = hotService.update(new LambdaUpdateWrapper<PmsHot>()
                .in(PmsHot::getId, hotIds)
                .eq(PmsHot::getVisible, ViewTypeEnum.VISIBLE)
                .set(PmsHot::getVisible, ViewTypeEnum.INVISIBLE));
        return Result.judge(status);
    }

    @ApiOperation(value = "删除热度")
    @DeleteMapping("/{ids}")
    @AutoLog(operateType = LogOperateTypeEnum.DELETE, logType = LogTypeEnum.OPERATE)
    public Result delete(
            @ApiParam("id集合") @PathVariable("ids") String ids) {
        boolean status = hotService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.judge(status);
    }
}
