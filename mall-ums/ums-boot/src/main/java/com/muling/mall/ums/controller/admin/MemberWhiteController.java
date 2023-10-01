package com.muling.mall.ums.controller.admin;

import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.mall.ums.pojo.entity.UmsMember;
import com.muling.mall.ums.pojo.entity.UmsWhite;
import com.muling.mall.ums.pojo.form.MemberAuthCreateForm;
import com.muling.mall.ums.pojo.form.MemberWhiteForm;
import com.muling.mall.ums.service.IUmsMemberService;
import com.muling.mall.ums.service.IUmsWhiteService;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.muling.common.constant.GlobalConstants.STATUS_YES;

@Api(tags = "admin-白名单权益")
@RestController("AdminMemberWhiteController")
@RequestMapping("/api/v1/white")
@Slf4j
@AllArgsConstructor
public class MemberWhiteController {

    private IUmsWhiteService whiteService;

    @ApiOperation(value = "白名单分页列表")
    @GetMapping
    public PageResult<UmsWhite> listMembersWithPage(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "ID") String memberId,
            @ApiParam(value = "手机号") String mobile,
            @ApiParam(value = "level") Integer level
    ) {
        LambdaQueryWrapper<UmsWhite> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(mobile!=null, UmsWhite::getMobile, mobile);
        wrapper.eq(memberId != null, UmsWhite::getMemberId, memberId);
        wrapper.eq(level != null, UmsWhite::getLevel, level);
        wrapper.orderByDesc(UmsWhite::getId);
        wrapper.orderByDesc(UmsWhite::getUpdated);
        //
        IPage<UmsWhite> result = whiteService.page(new Page<>(pageNum, pageSize), wrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "详情")
    @GetMapping("/{id}")
    public Result<UmsWhite> getMemberById(
            @PathVariable Long id
    ) {
        UmsWhite user = whiteService.getById(id);
        return Result.success(user);
    }

    @ApiOperation(value = "创建白名单权益")
    @PostMapping("/create/{memberId}")
    public Result create(
            @PathVariable Long memberId,
            @Valid @RequestBody MemberWhiteForm memberWhiteForm) {
        boolean result = whiteService.create(memberId, memberWhiteForm);
        return Result.success(result);
    }

    @ApiOperation(value = "根据MemberIds获得White列表")
    @GetMapping("/ids")
    public Result<List<UmsWhite>> getMemberByIds(
            @ApiParam("memberIds") @RequestParam(required = true) List<Long> memberIds) {
        if (memberIds == null || memberIds.isEmpty() || memberIds.size() > 50) {
            return Result.failed("member ID不能为空，且不能超过50个");
        }
        LambdaQueryWrapper<UmsWhite> wrapper = Wrappers.<UmsWhite>lambdaQuery().in(UmsWhite::getId, memberIds);
        List<UmsWhite> list = whiteService.list(wrapper);
        return Result.success(list);
    }

    @ApiOperation(value = "修改")
    @PutMapping(value = "/{id}")
    public <T> Result<T> update(
            @PathVariable Long id,
            @RequestBody UmsWhite umsWhite) {
        boolean status = whiteService.updateById(umsWhite);
        return Result.judge(status);
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("/{ids}")
    public <T> Result<T> delete(@PathVariable String ids) {
        boolean status = whiteService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.judge(status);
    }
    //
}
