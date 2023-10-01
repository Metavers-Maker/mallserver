package com.muling.admin.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.admin.pojo.entity.SysPathRule;
import com.muling.admin.pojo.form.PathRuleForm;
import com.muling.admin.service.ISysPathRuleService;
import com.muling.common.annotation.AutoLog;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = "admin-路径规则")
@RestController
@RequestMapping("/api/v1/path-rules")
@RequiredArgsConstructor
public class PathRuleController {

    private final ISysPathRuleService pathRuleService;

    @ApiOperation(value = "分页列表")
    @GetMapping("/page")
    public PageResult list(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam("名称") String name) {
        LambdaQueryWrapper<SysPathRule> queryWrapper = new LambdaQueryWrapper<SysPathRule>()
                .like(StrUtil.isNotBlank(name), SysPathRule::getName, name)
                .orderByDesc(SysPathRule::getUpdated)
                .orderByDesc(SysPathRule::getCreated);
        Page<SysPathRule> result = pathRuleService.page(new Page(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "详情")
    @GetMapping("/{id}")
    public Result detail(@PathVariable Integer id) {
        SysPathRule dict = pathRuleService.getById(id);
        return Result.success(dict);
    }

    @ApiOperation(value = "新增")
    @PostMapping
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    public Result add(@RequestBody PathRuleForm pathRuleForm) {
        boolean status = pathRuleService.save(pathRuleForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "修改")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    @PutMapping(value = "/{id}")
    public Result update(
            @PathVariable Long id,
            @RequestBody PathRuleForm pathRuleForm) {

        boolean status = pathRuleService.update(id, pathRuleForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("/{id}")
    @AutoLog(operateType = LogOperateTypeEnum.DELETE, logType = LogTypeEnum.OPERATE)
    public Result delete(@PathVariable Long id) {
        boolean status = pathRuleService.delete(id);
        return Result.judge(status);
    }

}
