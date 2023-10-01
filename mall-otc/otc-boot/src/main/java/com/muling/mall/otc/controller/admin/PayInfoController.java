package com.muling.mall.otc.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.mall.otc.pojo.entity.OtcPayInfo;
import com.muling.mall.otc.service.IPayInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "admin-支付信息")
@RestController("PayInfoController")
@RequestMapping("/api/v1/pay-info")
@RequiredArgsConstructor
public class PayInfoController {

    private final IPayInfoService payInfoService;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize
    ) {
        LambdaQueryWrapper<OtcPayInfo> queryWrapper = new LambdaQueryWrapper<OtcPayInfo>()
                .orderByDesc(OtcPayInfo::getUpdated)
                .orderByDesc(OtcPayInfo::getCreated);
        Page<OtcPayInfo> result = payInfoService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "详情")
    @GetMapping("/{id}")
    public Result getById(@ApiParam("id") @PathVariable Integer id) {
        OtcPayInfo config = payInfoService.getById(id);
        return Result.success(config);
    }

}
