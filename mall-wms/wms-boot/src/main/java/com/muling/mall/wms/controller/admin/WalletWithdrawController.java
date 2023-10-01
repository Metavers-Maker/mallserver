package com.muling.mall.wms.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.result.PageResult;
import com.muling.mall.wms.enums.WalletOpTypeEnum;
import com.muling.mall.wms.pojo.entity.WmsWalletLog;
import com.muling.mall.wms.pojo.entity.WmsWithdraw;
import com.muling.mall.wms.service.IWmsWalletLogService;
import com.muling.mall.wms.service.IWmsWithdrawService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Api(tags = "admin-钱包体现管理")
@RestController("WalletWithdrawController")
@RequestMapping("/api/v1/wallet/withdraw")
@RequiredArgsConstructor
public class WalletWithdrawController {

    private final IWmsWithdrawService wmsWithdrawService;

    @ApiOperation("分页列表")
    @GetMapping
    public PageResult page(@ApiParam(value = "页码", example = "1") Long pageNum,
                           @ApiParam(value = "每页数量", example = "10") Long pageSize,
                           @ApiParam(value = "会员ID") Long memberId,
                           @ApiParam(value = "币种") Integer[] coinTypes,
                           @ApiParam(value = "操作类型") WalletOpTypeEnum[] opTypes,
                           @ApiParam(value = "开始时间") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime started,
                           @ApiParam(value = "结束时间") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime ended) {

        LambdaQueryWrapper<WmsWithdraw> wrapper = Wrappers.<WmsWithdraw>lambdaQuery()
                .eq(memberId != null, WmsWithdraw::getMemberId, memberId)
//                .in(coinTypes != null, WmsWithdraw::getCoinType, coinTypes)
//                .in(opTypes != null, WmsWithdraw::getOpType, opTypes)
                .ge(started != null, WmsWithdraw::getCreated, started)
                .le(ended != null, WmsWithdraw::getCreated, ended)
                .orderByDesc(WmsWithdraw::getUpdated);
        IPage<WmsWithdraw> result = wmsWithdrawService.page(new Page(pageNum, pageSize), wrapper);
        return PageResult.success(result);
    }

}
