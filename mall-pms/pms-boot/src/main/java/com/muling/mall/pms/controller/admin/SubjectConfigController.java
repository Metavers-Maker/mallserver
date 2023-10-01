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
import com.muling.mall.pms.pojo.entity.PmsRnd;
import com.muling.mall.pms.pojo.entity.PmsSubject;
import com.muling.mall.pms.pojo.entity.PmsSubjectConfig;
import com.muling.mall.pms.pojo.form.RndForm;
import com.muling.mall.pms.pojo.form.SubjectConfigFormDTO;
import com.muling.mall.pms.service.IPmsRndService;
import com.muling.mall.pms.service.IPmsSubjectConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Api(tags = "admin-系列配置管理")
@RestController("SubjectConfigController")
@RequestMapping("/api/v1/subject/config")
@RequiredArgsConstructor
public class SubjectConfigController {

    private final IPmsSubjectConfigService subjectConfigService;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "Subject ID") Long subjectId,
            @ApiParam(value = "Spu ID") Long spuId
    ) {
        LambdaQueryWrapper<PmsSubjectConfig> queryWrapper = new LambdaQueryWrapper<PmsSubjectConfig>()
                .eq(subjectId!=null,PmsSubjectConfig::getSubjectId,subjectId)
                .eq(spuId!=null,PmsSubjectConfig::getSpuId,spuId)
                .orderByDesc(PmsSubjectConfig::getUpdated)
                .orderByDesc(PmsSubjectConfig::getCreated);
        Page<PmsSubjectConfig> result = subjectConfigService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "详情")
    @GetMapping("/{id}")
    public Result getById(@ApiParam("id") @PathVariable Integer id) {
        PmsSubjectConfig subjectConfig = subjectConfigService.getById(id);
        return Result.success(subjectConfig);
    }

    @ApiOperation(value = "新增")
    @PostMapping
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    public Result add(
            @RequestBody SubjectConfigFormDTO subjectConfigFormDTO) {
        PmsSubjectConfig subjectConfig = subjectConfigService.save(subjectConfigFormDTO);
        return Result.success(subjectConfig);
    }

    @ApiOperation(value = "修改")
    @PutMapping(value = "/{id}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result updateById(
            @ApiParam("id") @PathVariable Long id,
            @RequestBody SubjectConfigFormDTO subjectConfigFormDTO) {
        boolean status = subjectConfigService.updateById(id, subjectConfigFormDTO);
        return Result.judge(status);
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("/{ids}")
    @AutoLog(operateType = LogOperateTypeEnum.DELETE, logType = LogTypeEnum.OPERATE)
    public Result delete(
            @ApiParam("id集合") @PathVariable("ids") String ids) {
        boolean status = subjectConfigService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.judge(status);
    }

    @ApiOperation(value = "显示")
    @PutMapping("/display")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result display(
            @ApiParam("ids") @RequestParam(required = true) List<Long> ids) {
        boolean status = subjectConfigService.update(new LambdaUpdateWrapper<PmsSubjectConfig>()
                .in(PmsSubjectConfig::getId, ids)
                .set(PmsSubjectConfig::getVisible, ViewTypeEnum.VISIBLE));
        return Result.judge(status);
    }

    @ApiOperation(value = "隐藏")
    @PutMapping("/hide")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result hide(
            @ApiParam("ids") @RequestParam(required = true) List<Long> ids) {
        boolean status = subjectConfigService.update(new LambdaUpdateWrapper<PmsSubjectConfig>()
                .in(PmsSubjectConfig::getId, ids)
                .set(PmsSubjectConfig::getVisible, ViewTypeEnum.INVISIBLE));
        return Result.judge(status);
    }
}
