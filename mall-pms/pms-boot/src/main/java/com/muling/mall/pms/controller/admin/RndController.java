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
import com.muling.mall.pms.pojo.form.RndForm;
import com.muling.mall.pms.service.IPmsRndService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Api(tags = "admin-盲盒规则管理")
@RestController("RndController")
@RequestMapping("/api/v1/rnds")
@RequiredArgsConstructor
public class RndController {

    private final IPmsRndService rndService;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "物品配置ID") Long target,
            @ApiParam("名称") String name
    ) {
        LambdaQueryWrapper<PmsRnd> queryWrapper = new LambdaQueryWrapper<PmsRnd>()
                .eq(target!=null,PmsRnd::getTarget,target)
                .like(StrUtil.isNotBlank(name), PmsRnd::getName, name)
                .orderByDesc(PmsRnd::getUpdated)
                .orderByDesc(PmsRnd::getCreated);
        Page<PmsRnd> result = rndService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "详情")
    @GetMapping("/{id}")
    public Result getById(@ApiParam("id") @PathVariable Integer id) {
        PmsRnd rnd = rndService.getById(id);
        return Result.success(rnd);
    }

    @ApiOperation(value = "新增")
    @PostMapping
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    public Result add(
            @RequestBody RndForm rndForm) {
        boolean status = rndService.save(rndForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "修改")
    @PutMapping(value = "/{id}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result updateById(
            @ApiParam("id") @PathVariable Long id,
            @RequestBody RndForm rndForm) {
        boolean status = rndService.updateById(id, rndForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("/{ids}")
    @AutoLog(operateType = LogOperateTypeEnum.DELETE, logType = LogTypeEnum.OPERATE)
    public Result delete(
            @ApiParam("id集合") @PathVariable("ids") String ids) {
        boolean status = rndService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.judge(status);
    }
}
