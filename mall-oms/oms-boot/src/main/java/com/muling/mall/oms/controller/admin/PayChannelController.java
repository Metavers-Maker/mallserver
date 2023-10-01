package com.muling.mall.oms.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.annotation.AutoLog;
import com.muling.common.base.BasePageQuery;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.mall.oms.enums.PayChannelStatusEnum;
import com.muling.mall.oms.pojo.entity.OmsPayChannel;
import com.muling.mall.oms.pojo.form.ChannelForm;
import com.muling.mall.oms.service.IPayChannelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "admin-支付渠道管理")
@RestController("PayChannelController")
@RequestMapping("/api/v1/pay/channels")
@Slf4j
@AllArgsConstructor
public class PayChannelController {

    private final IPayChannelService payChannelService;


    @ApiOperation("分页列表")
    @GetMapping
    public PageResult page(BasePageQuery queryParams) {
        LambdaQueryWrapper<OmsPayChannel> wrapper = Wrappers.<OmsPayChannel>lambdaQuery();
        IPage<OmsPayChannel> result = payChannelService.page(new Page(queryParams.getPageNum(), queryParams.getPageSize()), wrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "新增")
    @PostMapping
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    public Result add(
            @RequestBody ChannelForm channelForm) {
        boolean status = payChannelService.save(channelForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "修改")
    @PutMapping(value = "/{id}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result updateById(
            @ApiParam("品牌id") @PathVariable Long id,
            @RequestBody ChannelForm channelForm) {
        boolean status = payChannelService.updateById(id, channelForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "上架")
    @PutMapping("/up")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result up(
            @ApiParam("ids") @RequestParam(required = true) List<Long> channelIds) {
        boolean status = payChannelService.update(new LambdaUpdateWrapper<OmsPayChannel>()
                .in(OmsPayChannel::getId, channelIds)
                .eq(OmsPayChannel::getStatus, PayChannelStatusEnum.DOWN)
                .set(OmsPayChannel::getStatus, PayChannelStatusEnum.UP));
        return Result.judge(status);
    }

    @ApiOperation(value = "下架")
    @PutMapping("/down")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result down(
            @ApiParam("ids") @RequestParam(required = true) List<Long> channelIds) {
        boolean status = payChannelService.update(new LambdaUpdateWrapper<OmsPayChannel>()
                .in(OmsPayChannel::getId, channelIds)
                .eq(OmsPayChannel::getStatus, PayChannelStatusEnum.UP)
                .set(OmsPayChannel::getStatus, PayChannelStatusEnum.DOWN));
        return Result.judge(status);
    }

}
