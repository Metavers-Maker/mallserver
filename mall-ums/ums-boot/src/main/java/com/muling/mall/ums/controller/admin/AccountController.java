package com.muling.mall.ums.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.annotation.AutoLog;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.mall.ums.pojo.entity.UmsMember;
import com.muling.mall.ums.pojo.entity.UmsWhite;
import com.muling.mall.ums.pojo.form.AdminRegisterForm;
import com.muling.mall.ums.service.IUmsMemberService;
import com.muling.mall.ums.service.IUmsWhiteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Api(tags = "admin-账号")
@RestController("AdminAccountController")
@RequestMapping("/api/v1/account")
@Slf4j
@AllArgsConstructor
public class AccountController {

    private IUmsMemberService memberService;

    private IUmsWhiteService whiteService;

    @ApiOperation(value = "新增")
    @PostMapping
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.REGIST)
    public Result add(@RequestBody @Validated AdminRegisterForm adminRegisterForm) {
        Long result = memberService.adminAddMember(adminRegisterForm);
        return Result.success(result);
    }

    @ApiOperation(value = "修改白名单等级")
    @PutMapping("/white/update")
    public Result whiteUpdate( @ApiParam(value = "电话", example = "") String mobile,
                               @ApiParam(value = "等级", example = "0") Integer level) {
        boolean f = whiteService.updateLevel(level,mobile);
        return Result.success(f);
    }

    @ApiOperation(value = "白名单分页列表")
    @GetMapping("/white/page")
    public PageResult<UmsWhite> listWhiteWithPage(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "手机号") String mobile,
            @ApiParam(value = "Level") Integer level,
            @ApiParam(value = "开始时间") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime started,
            @ApiParam(value = "结束时间") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime ended
    ) {
        LambdaQueryWrapper<UmsWhite> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(mobile!=null, UmsWhite::getMobile, mobile);
        wrapper.eq(level != null, UmsWhite::getLevel, level);
        wrapper.ge(started != null, UmsWhite::getCreated, started);
        wrapper.le(ended != null, UmsWhite::getCreated, ended);
        wrapper.orderByDesc(UmsWhite::getLevel);
        wrapper.orderByDesc(UmsWhite::getUpdated);
        //
        IPage<UmsWhite> result = whiteService.page(new Page<>(pageNum, pageSize), wrapper);
        return PageResult.success(result);
    }

}
