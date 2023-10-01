package com.muling.mall.wms.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.annotation.AutoLog;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.enums.StatusEnum;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.mall.wms.pojo.entity.WmsSwapConfig;
import com.muling.mall.wms.pojo.form.admin.SwapConfigForm;
import com.muling.mall.wms.service.IWmsSwapConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Api(tags = "admin-兑换管理")
@RestController("SwapConfigController")
@RequestMapping("/api/v1/swap-config")
@RequiredArgsConstructor
public class SwapConfigController {

    private final IWmsSwapConfigService swapConfigService;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "提示") String remark
    ) {
        LambdaQueryWrapper<WmsSwapConfig> queryWrapper = new LambdaQueryWrapper<WmsSwapConfig>()
                .like(StrUtil.isNotBlank(remark), WmsSwapConfig::getRemark, remark)
                .orderByDesc(WmsSwapConfig::getUpdated)
                .orderByDesc(WmsSwapConfig::getCreated);
        Page<WmsSwapConfig> result = swapConfigService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "详情")
    @GetMapping("/{id}")
    public Result getById(@ApiParam("id") @PathVariable Integer id) {
        WmsSwapConfig swapConfig = swapConfigService.getById(id);
        return Result.success(swapConfig);
    }

    @ApiOperation(value = "新增")
    @PostMapping
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    public Result add(
            @RequestBody SwapConfigForm swapConfigForm) {
        boolean status = swapConfigService.add(swapConfigForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "修改")
    @PutMapping(value = "/{id}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result updateById(
            @ApiParam("轮播id") @PathVariable Long id,
            @RequestBody SwapConfigForm swapConfigForm) {

        boolean status = swapConfigService.update(id, swapConfigForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "修改显示状态")
    @PatchMapping(value = "/{id}")
    public Result display(
            @PathVariable Long id, StatusEnum status) {
        boolean result = swapConfigService.update(id, status);
        return Result.judge(result);
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("/{ids}")
    public Result deleteAddress(
            @ApiParam("IDS以英文逗号(,)分割") @PathVariable String ids
    ) {
        List<String> list = Arrays.asList(ids.split(","));
        boolean status = swapConfigService.delete(list);
        return Result.judge(status);
    }

}
