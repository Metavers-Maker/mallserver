package com.muling.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.admin.pojo.entity.SysDictItem;
import com.muling.admin.pojo.form.DictItemForm;
import com.muling.admin.service.ISysDictItemService;
import com.muling.common.annotation.AutoLog;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.result.Result;
import com.muling.common.web.domain.OptionVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "admin-字典项")
@RestController
@RequestMapping("/api/v1/dict-items")
@RequiredArgsConstructor
public class DictItemController {

    private final ISysDictItemService dictItemService;

    @ApiOperation(value = "分页列表")
    @GetMapping("/page")
    public Result getPageList(
            Integer page,
            Integer limit,
            String name,
            String dictCode
    ) {
        IPage<SysDictItem> result = dictItemService.list(new Page<>(page, limit),
                new SysDictItem().setName(name).setDictCode(dictCode));
        return Result.success(result.getRecords(), result.getTotal());
    }

    @ApiOperation(value = "字典项详细")
    @GetMapping("/{id}")
    public Result detail(@PathVariable Integer id) {
        SysDictItem dictItem = dictItemService.getById(id);
        return Result.success(dictItem);
    }

    @ApiOperation(value = "新增字典数据项")
    @PostMapping
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    public Result add(@RequestBody DictItemForm dictItemForm) {
        boolean status = dictItemService.saveDictItem(dictItemForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "更新字典数据项")
    @PutMapping(value = "/{id}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result update(
            @PathVariable Long id,
            @RequestBody DictItemForm dictItemForm) {
        boolean status = dictItemService.updateDictItem(id, dictItemForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "删除字典")
    @DeleteMapping("/{ids}")
    @AutoLog(operateType = LogOperateTypeEnum.DELETE, logType = LogTypeEnum.OPERATE)
    public Result delete(@PathVariable String ids) {
        boolean status = dictItemService.deleteDictItems(ids);
        return Result.judge(status);
    }

    @ApiOperation(value = "根据字典编码获取字典数据项")
    @GetMapping("/select_list")
    public Result<List<OptionVO>> getDictItemsByDictCode(@ApiParam("字典编码") @RequestParam String dictCode) {
        List<OptionVO> list = dictItemService.listDictItemsByDictCode(dictCode);
        return Result.success(list);
    }

    @ApiOperation(value = "修改显示状态")
    @PatchMapping(value = "/{id}")
    public Result patch(@PathVariable Long id, Integer visible) {
        boolean result = dictItemService.updateVisible(id, visible);
        return Result.judge(result);
    }
}
