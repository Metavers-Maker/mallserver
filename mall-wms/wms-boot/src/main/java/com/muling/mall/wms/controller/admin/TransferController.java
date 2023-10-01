package com.muling.mall.wms.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.annotation.AutoLog;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.enums.StatusEnum;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.mall.wms.pojo.entity.WmsTransferConfig;
import com.muling.mall.wms.pojo.form.admin.TransferConfigForm;
import com.muling.mall.wms.service.IWmsTransferConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Api(tags = "admin-转赠管理")
@RestController("TransferController")
@RequestMapping("/api/v1/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final IWmsTransferConfigService transferConfigService;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "提示") String remark
    ) {
        LambdaQueryWrapper<WmsTransferConfig> queryWrapper = new LambdaQueryWrapper<WmsTransferConfig>()
                .like(StrUtil.isNotBlank(remark), WmsTransferConfig::getRemark, remark)
                .orderByDesc(WmsTransferConfig::getUpdated)
                .orderByDesc(WmsTransferConfig::getCreated);
        Page<WmsTransferConfig> result = transferConfigService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "详情")
    @GetMapping("/{id}")
    public Result getById(@ApiParam("id") @PathVariable Integer id) {
        WmsTransferConfig brand = transferConfigService.getById(id);
        return Result.success(brand);
    }

    @ApiOperation(value = "新增")
    @PostMapping
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    public Result add(
            @RequestBody TransferConfigForm transferConfigForm) {
        boolean status = transferConfigService.save(transferConfigForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "修改")
    @PutMapping(value = "/{id}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result updateById(
            @ApiParam("轮播id") @PathVariable Long id,
            @RequestBody TransferConfigForm transferConfigForm) {

        boolean status = transferConfigService.updateById(id, transferConfigForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "可用")
    @PutMapping("/enable")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result display(
            @ApiParam("ids") @RequestParam(required = true) List<Long> ids) {
        boolean status = transferConfigService.update(new LambdaUpdateWrapper<WmsTransferConfig>()
                .in(WmsTransferConfig::getId, ids)
                .eq(WmsTransferConfig::getStatus, StatusEnum.DISABLED)
                .set(WmsTransferConfig::getStatus, StatusEnum.ENABLE));
        return Result.judge(status);
    }

    @ApiOperation(value = "不可用")
    @PutMapping("/disabled")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result hide(
            @ApiParam("ids") @RequestParam(required = true) List<Long> ids) {
        boolean status = transferConfigService.update(new LambdaUpdateWrapper<WmsTransferConfig>()
                .in(WmsTransferConfig::getId, ids)
                .eq(WmsTransferConfig::getStatus, StatusEnum.ENABLE)
                .set(WmsTransferConfig::getStatus, StatusEnum.DISABLED));
        return Result.judge(status);
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("/{ids}")
    @AutoLog(operateType = LogOperateTypeEnum.DELETE, logType = LogTypeEnum.OPERATE)
    public Result delete(
            @ApiParam("id集合") @PathVariable("ids") String ids) {
        boolean status = transferConfigService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.judge(status);
    }

}
