package com.muling.mall.pms.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.annotation.AutoLog;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.mall.pms.common.enums.StatusEnum;
import com.muling.mall.pms.common.enums.ViewTypeEnum;
import com.muling.mall.pms.pojo.entity.PmsBrand;
import com.muling.mall.pms.pojo.form.BrandForm;
import com.muling.mall.pms.service.IPmsBrandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author <a href="mailto:xianrui0365@163.com">haoxr</a>
 */
@Api(tags = "admin-作者管理")
@RestController("adminBrandController")
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandController {

    private final IPmsBrandService brandService;

    @ApiOperation(value = "作者列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam("角色(0-作者,1-发行方)") Integer role,
            @ApiParam("作者名称") String name
    ) {
        LambdaQueryWrapper<PmsBrand> queryWrapper = new LambdaQueryWrapper<PmsBrand>()
                .eq(role!=null,PmsBrand::getRole,role)
                .like(StrUtil.isNotBlank(name), PmsBrand::getName, name)
                .orderByDesc(PmsBrand::getSort)
                .orderByDesc(PmsBrand::getCreated);
        Page<PmsBrand> result = brandService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "作者列表")
    @GetMapping
    public Result list(
            @ApiParam("角色") Integer role,
            @ApiParam("作者名称") String name
    ) {
        LambdaQueryWrapper<PmsBrand> queryWrapper = new LambdaQueryWrapper<PmsBrand>()
                .eq(role!=null,PmsBrand::getRole,role)
                .like(StrUtil.isNotBlank(name), PmsBrand::getName, name)
                .orderByDesc(PmsBrand::getSort)
                .orderByDesc(PmsBrand::getCreated);
        List<PmsBrand> result = brandService.list(queryWrapper);
        return Result.success(result);
    }


    @ApiOperation(value = "根据品牌IDS获取作者列表")
    @GetMapping("/ids")
    public Result<List<PmsBrand>> listByBrandIds(
            @ApiParam("Brand ID") @RequestParam(required = true) List<Long> brandIds) {
        if (brandIds == null || brandIds.isEmpty() || brandIds.size() > 20) {
            return Result.failed("Brand ID不能为空，且不能超过20个");
        }

        List<PmsBrand> result = brandService.list(Wrappers.<PmsBrand>lambdaQuery().in(PmsBrand::getId, brandIds));
        return Result.success(result);
    }

    @ApiOperation(value = "作者详情")
    @GetMapping("/{id}")
    public Result getById(@ApiParam("品牌id") @PathVariable Integer id) {
        PmsBrand brand = brandService.getById(id);
        return Result.success(brand);
    }

    @ApiOperation(value = "新增作者/发行方")
    @PostMapping
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    public Result add(
            @RequestBody BrandForm brandForm) {
        boolean status = brandService.save(brandForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "修改作者")
    @PutMapping(value = "/{id}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result updateById(
            @ApiParam("品牌id") @PathVariable Long id,
            @RequestBody BrandForm brandForm) {
        boolean status = brandService.updateById(id, brandForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "上架作者")
    @PutMapping("/up")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result up(
            @ApiParam("brand ids") @RequestParam(required = true) List<Long> brandIds) {
        boolean status = brandService.update(new LambdaUpdateWrapper<PmsBrand>()
                .in(PmsBrand::getId, brandIds)
                .eq(PmsBrand::getStatus, StatusEnum.DOWN)
                .set(PmsBrand::getStatus, StatusEnum.UP));
        return Result.judge(status);
    }

    @ApiOperation(value = "显示")
    @PutMapping("/display")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result display(
            @ApiParam("brand ids") @RequestParam(required = true) List<Long> brandIds) {
        boolean status = brandService.update(new LambdaUpdateWrapper<PmsBrand>()
                .in(PmsBrand::getId, brandIds)
                .eq(PmsBrand::getStatus, StatusEnum.UP)
                .set(PmsBrand::getVisible, ViewTypeEnum.VISIBLE));
        return Result.judge(status);
    }

    @ApiOperation(value = "隐藏")
    @PutMapping("/hide")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result hide(
            @ApiParam("brand ids") @RequestParam(required = true) List<Long> brandIds) {
        boolean status = brandService.update(new LambdaUpdateWrapper<PmsBrand>()
                .in(PmsBrand::getId, brandIds)
                .eq(PmsBrand::getVisible, ViewTypeEnum.VISIBLE)
                .set(PmsBrand::getVisible, ViewTypeEnum.INVISIBLE));
        return Result.judge(status);
    }

    @ApiOperation(value = "删除作者")
    @DeleteMapping("/{ids}")
    @AutoLog(operateType = LogOperateTypeEnum.DELETE, logType = LogTypeEnum.OPERATE)
    public Result delete(
            @ApiParam("id集合") @PathVariable("ids") String ids) {

        boolean status = brandService.remove(new LambdaQueryWrapper<PmsBrand>()
                .eq(PmsBrand::getStatus, StatusEnum.DOWN)
                .in(PmsBrand::getId, ids.split(",")));
        return Result.judge(status);
    }

}
