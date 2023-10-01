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
import com.muling.mall.bms.pojo.entity.OmsCompoundConfig;
import com.muling.mall.bms.pojo.form.admin.CompoundConfigForm;
import com.muling.mall.bms.service.ICompoundConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Api(tags = "admin-合成配置")
@RestController("CompoundConfigController")
@RequestMapping("/api/v1/compound-config")
@RequiredArgsConstructor
public class CompoundConfigController {

    private final ICompoundConfigService compoundConfigService;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize
    ) {
        LambdaQueryWrapper<OmsCompoundConfig> queryWrapper = new LambdaQueryWrapper<OmsCompoundConfig>()
                .orderByDesc(OmsCompoundConfig::getUpdated)
                .orderByDesc(OmsCompoundConfig::getCreated);
        Page<OmsCompoundConfig> result = compoundConfigService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "详情")
    @GetMapping("/{id}")
    public Result getById(@ApiParam("id") @PathVariable Integer id) {
        OmsCompoundConfig config = compoundConfigService.getById(id);
        return Result.success(config);
    }

    @ApiOperation(value = "新增")
    @PostMapping
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    public Result add(
            @RequestBody CompoundConfigForm compoundConfigForm) {
        boolean status = compoundConfigService.save(compoundConfigForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "修改")
    @PutMapping(value = "/{id}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result updateById(
            @ApiParam("轮播id") @PathVariable Long id,
            @RequestBody CompoundConfigForm compoundConfigForm) {

        boolean status = compoundConfigService.updateById(id, compoundConfigForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "可用")
    @PutMapping("/enable")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result display(
            @ApiParam("ids") @RequestParam(required = true) List<Long> ids) {
        boolean status = compoundConfigService.update(new LambdaUpdateWrapper<OmsCompoundConfig>()
                .in(OmsCompoundConfig::getId, ids)
                .eq(OmsCompoundConfig::getStatus, StatusEnum.DISABLED)
                .set(OmsCompoundConfig::getStatus, StatusEnum.ENABLE));
        return Result.judge(status);
    }

    @ApiOperation(value = "不可用")
    @PutMapping("/disabled")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result hide(
            @ApiParam("ids") @RequestParam(required = true) List<Long> ids) {
        boolean status = compoundConfigService.update(new LambdaUpdateWrapper<OmsCompoundConfig>()
                .in(OmsCompoundConfig::getId, ids)
                .eq(OmsCompoundConfig::getStatus, StatusEnum.ENABLE)
                .set(OmsCompoundConfig::getStatus, StatusEnum.DISABLED));
        return Result.judge(status);
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("/{ids}")
    @AutoLog(operateType = LogOperateTypeEnum.DELETE, logType = LogTypeEnum.OPERATE)
    public Result delete(
            @ApiParam("id集合") @PathVariable("ids") String ids) {
        boolean status = compoundConfigService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.judge(status);
    }
}
