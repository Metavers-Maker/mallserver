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
import com.muling.mall.pms.pojo.entity.PmsAirdropConfig;
import com.muling.mall.pms.pojo.form.AirdropConfigForm;
import com.muling.mall.pms.service.IPmsAirdropConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Api(tags = "admin-空投活动管理")
@RestController("AirdropConfigController")
@RequestMapping("/api/v1/airdrop/config")
@RequiredArgsConstructor
public class AirdropConfigController {

    private final IPmsAirdropConfigService airdropConfigService;

    @ApiOperation(value = "活动列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "活动状态", example = "0") Integer status) {

        LambdaQueryWrapper<PmsAirdropConfig> queryWrapper = new LambdaQueryWrapper<PmsAirdropConfig>()
                .eq(status != null, PmsAirdropConfig::getStatus, status)
                .orderByDesc(PmsAirdropConfig::getSort)
                .orderByDesc(PmsAirdropConfig::getUpdated);
        Page<PmsAirdropConfig> result = airdropConfigService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "活动配置详情")
    @GetMapping("/{id}")
    public Result getById(@ApiParam("活动id") @PathVariable Integer id) {
        PmsAirdropConfig airdropConfig = airdropConfigService.getById(id);
        return Result.success(airdropConfig);
    }

    @ApiOperation(value = "新增活动配置")
    @PostMapping
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    public Result add(
            @RequestBody AirdropConfigForm airdropConfigForm) {
        boolean status = airdropConfigService.add(airdropConfigForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "修改活动配置")
    @PutMapping(value = "/{id}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result updateById(
            @ApiParam("活动id") @PathVariable Long id,
            @RequestBody AirdropConfigForm airdropConfigForm) {

        boolean status = airdropConfigService.updateById(id, airdropConfigForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "设置活动状态")
    @PutMapping("/status/{status}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result setStatus(
            @ApiParam("活动状态") @PathVariable Integer status,
            @ApiParam("活动id") @RequestParam(required = true) List<Long> ids) {
        boolean ret = airdropConfigService.update(new LambdaUpdateWrapper<PmsAirdropConfig>()
                .in(PmsAirdropConfig::getId, ids)
                .set(PmsAirdropConfig::getStatus, status));
        return Result.judge(ret);
    }

    @ApiOperation(value = "删除活动配置")
    @DeleteMapping("/{ids}")
    @AutoLog(operateType = LogOperateTypeEnum.DELETE, logType = LogTypeEnum.OPERATE)
    public Result delete(
            @ApiParam("id集合") @PathVariable("ids") String ids) {
        boolean status = airdropConfigService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.judge(status);
    }

}
