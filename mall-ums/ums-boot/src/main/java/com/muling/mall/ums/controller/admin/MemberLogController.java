package com.muling.mall.ums.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.result.PageResult;
import com.muling.mall.ums.enums.MemberLogTypeEnum;
import com.muling.mall.ums.pojo.entity.UmsMemberLog;
import com.muling.mall.ums.service.IUmsMemberLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Api(tags = "admin-会员日志")
@RestController("AdminMemberLogController")
@RequestMapping("/api/v1/users/log")
@Slf4j
@AllArgsConstructor
public class MemberLogController {

    private IUmsMemberLogService memberLogService;

    @ApiOperation(value = "分页列表")
    @GetMapping
    public PageResult<UmsMemberLog> listMembersWithPage(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "会员Id") Long memberId,
            @ApiParam(value = "类型") MemberLogTypeEnum[] types,
            @ApiParam(value = "开始时间") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime started,
            @ApiParam(value = "结束时间") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime ended
    ) {

        LambdaQueryWrapper<UmsMemberLog> wrapper = Wrappers.<UmsMemberLog>lambdaQuery()
                .eq(memberId != null, UmsMemberLog::getMemberId, memberId)
                .in(types != null, UmsMemberLog::getType, types)
                .ge(started != null, UmsMemberLog::getCreated, started)
                .le(ended != null, UmsMemberLog::getCreated, ended)
                .orderByDesc(UmsMemberLog::getMemberId)
                .orderByDesc(UmsMemberLog::getCreated);

        IPage<UmsMemberLog> result = memberLogService.page(new Page<>(pageNum, pageSize), wrapper);
        return PageResult.success(result);
    }
}
