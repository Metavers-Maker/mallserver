package com.muling.admin.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.admin.pojo.entity.SysDict;
import com.muling.admin.pojo.form.DictForm;
import com.muling.admin.service.ISysDictService;
import com.muling.common.annotation.AutoLog;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "admin-字典")
@RestController
@RequestMapping("/api/v1/dicts")
@RequiredArgsConstructor
public class DictController {

    private final ISysDictService dictService;

    @ApiOperation(value = "字典分页列表")
    @GetMapping("/page")
    public Result list(
            @ApiParam("页码") Integer page,
            @ApiParam("每页数量") Integer limit,
            @ApiParam("字典名称") String name) {
        Page<SysDict> result = dictService.page(new Page<>(page, limit), new LambdaQueryWrapper<SysDict>()
                .like(StrUtil.isNotBlank(name), SysDict::getName, StrUtil.trimToNull(name))
                .orderByDesc(SysDict::getUpdated)
                .orderByDesc(SysDict::getCreated));
        return Result.success(result.getRecords(), result.getTotal());
    }

    @ApiOperation(value = "列表")
    @GetMapping
    public Result list() {
        List<SysDict> list = dictService.list(new LambdaQueryWrapper<SysDict>()
                .orderByDesc(SysDict::getUpdated)
                .orderByDesc(SysDict::getCreated));
        return Result.success(list);
    }


    @ApiOperation(value = "字典详情")
    @GetMapping("/{id}")
    public Result detail(@PathVariable Integer id) {
        SysDict dict = dictService.getById(id);
        return Result.success(dict);
    }

    @ApiOperation(value = "新增字典")
    @PostMapping
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    public Result add(@RequestBody DictForm dictForm) {
        boolean status = dictService.saveDict(dictForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "修改字典")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    @PutMapping(value = "/{id}")
    public Result update(
            @PathVariable Long id,
            @RequestBody DictForm dictForm) {

        boolean status = dictService.updateDict(id, dictForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "删除字典")
    @DeleteMapping("/{ids}")
    @AutoLog(operateType = LogOperateTypeEnum.DELETE, logType = LogTypeEnum.OPERATE)
    public Result delete(@PathVariable String ids) {
        boolean status = dictService.deleteDicts(ids);
        return Result.judge(status);
    }

    @ApiOperation(value = "修改显示状态")
    @PatchMapping(value = "/{id}")
    public Result patch(@PathVariable Long id, Integer visible) {
        boolean result = dictService.updateVisible(id, visible);
        return Result.judge(result);
    }

}
