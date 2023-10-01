package com.muling.mall.bms.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.annotation.AutoLog;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.mall.bms.enums.StatusEnum;
import com.muling.mall.bms.pojo.entity.OmsFarmPool;
import com.muling.mall.bms.pojo.form.admin.StakeConfigForm;
import com.muling.mall.bms.service.IFarmPoolService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

//@Api(tags = "admin-锁仓配置")
@RestController("FarmPoolController")
@RequestMapping("/api/v1/farm-pool")
@RequiredArgsConstructor
public class FarmPoolController {

    private final IFarmPoolService farmPoolService;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize
    ) {
        LambdaQueryWrapper<OmsFarmPool> queryWrapper = new LambdaQueryWrapper<OmsFarmPool>()
                .orderByDesc(OmsFarmPool::getUpdated)
                .orderByDesc(OmsFarmPool::getCreated);
        Page<OmsFarmPool> result = farmPoolService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "详情")
    @GetMapping("/{id}")
    public Result getById(@ApiParam("id") @PathVariable Integer id) {
        OmsFarmPool config = farmPoolService.getById(id);
        return Result.success(config);
    }

    @ApiOperation(value = "新增")
    @PostMapping
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    public Result add(
            @RequestBody StakeConfigForm configForm) {
        boolean status = farmPoolService.save(configForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "修改")
    @PutMapping(value = "/{id}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result updateById(
            @ApiParam("轮播id") @PathVariable Long id,
            @RequestBody StakeConfigForm configForm) {

        boolean status = farmPoolService.updateById(id, configForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "可用")
    @PutMapping("/enable")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result display(
            @ApiParam("ids") @RequestParam(required = true) List<Long> ids) {
        boolean status = farmPoolService.update(new LambdaUpdateWrapper<OmsFarmPool>()
                .in(OmsFarmPool::getId, ids)
                .eq(OmsFarmPool::getStatus, StatusEnum.DISABLED)
                .set(OmsFarmPool::getStatus, StatusEnum.ENABLE));
        return Result.judge(status);
    }

    @ApiOperation(value = "不可用")
    @PutMapping("/disabled")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result hide(
            @ApiParam("ids") @RequestParam(required = true) List<Long> ids) {
        boolean status = farmPoolService.update(new LambdaUpdateWrapper<OmsFarmPool>()
                .in(OmsFarmPool::getId, ids)
                .eq(OmsFarmPool::getStatus, StatusEnum.ENABLE)
                .set(OmsFarmPool::getStatus, StatusEnum.DISABLED));
        return Result.judge(status);
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("/{ids}")
    @AutoLog(operateType = LogOperateTypeEnum.DELETE, logType = LogTypeEnum.OPERATE)
    public Result delete(
            @ApiParam("id集合") @PathVariable("ids") String ids) {
        boolean status = farmPoolService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.judge(status);
    }
}
