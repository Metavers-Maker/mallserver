package com.muling.mall.ums.controller.admin;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.enums.StatusEnum;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.common.util.BeanUtil;
import com.muling.common.util.DateUtil;
import com.muling.common.util.ValidateUtil;
import com.muling.common.web.util.ExcelExportUtil;
import com.muling.mall.ums.constant.MemberHeader;
import com.muling.mall.ums.enums.MemberAuthStatusEnum;
import com.muling.mall.ums.pojo.dto.MemberDTO;
import com.muling.mall.ums.pojo.dto.MemberListByIds;
import com.muling.mall.ums.pojo.entity.UmsMember;
import com.muling.mall.ums.pojo.vo.MemberExportVO;
import com.muling.mall.ums.service.IUmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.muling.common.constant.GlobalConstants.STATUS_YES;
import static com.muling.common.util.DateUtil.YYYY_MM_DD_HH_MM_SS;

@Api(tags = "admin-会员")
@RestController("AdminMemberController")
@RequestMapping("/api/v1/users")
@Slf4j
@AllArgsConstructor
public class MemberController {

    private IUmsMemberService memberService;

    @ApiOperation(value = "会员分页列表")
    @GetMapping
    public PageResult<UmsMember> listMembersWithPage(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "ID") String memberId,
            @ApiParam(value = "会员昵称") String nickName,
            @ApiParam(value = "手机号") String mobile,
            @ApiParam(value = "alipay") String alipay,
            @ApiParam(value = "wechat") String wechat,
            @ApiParam(value = "Status") Integer status,
            @ApiParam(value = "认证状态[0.未认证 1.认证中 2.重新认证 3.已认证]") Integer authStatus,
            @ApiParam(value = "开始时间") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime started,
            @ApiParam(value = "结束时间") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime ended
    ) {
        LambdaQueryWrapper<UmsMember> wrapper = Wrappers.lambdaQuery();
        wrapper.like(StrUtil.isNotBlank(alipay), UmsMember::getAlipay, alipay);
        wrapper.like(StrUtil.isNotBlank(wechat), UmsMember::getWechat, wechat);
        wrapper.eq(nickName!=null, UmsMember::getNickName, nickName);
        wrapper.eq(mobile!=null, UmsMember::getMobile, mobile);
        wrapper.eq(memberId != null, UmsMember::getId, memberId);
        wrapper.eq(authStatus != null, UmsMember::getAuthStatus, authStatus);
        wrapper.eq(status != null, UmsMember::getStatus, status);
        wrapper.ge(started != null, UmsMember::getCreated, started);
        wrapper.le(ended != null, UmsMember::getCreated, ended);
        wrapper.orderByDesc(UmsMember::getId);
        wrapper.orderByDesc(UmsMember::getUpdated);
        //
        IPage<UmsMember> result = memberService.page(new Page<>(pageNum, pageSize), wrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "详情")
    @GetMapping("/{id}")
    public Result<UmsMember> getMemberById(
            @PathVariable Long id
    ) {
        UmsMember user = memberService.getById(id);
        return Result.success(user);
    }

    @ApiOperation(value = "根据MemberIds获得Member列表")
    @GetMapping("/ids")
    public Result<List<UmsMember>> getMemberByIds(
            @ApiParam("memberIds") @RequestParam(required = true) List<Long> memberIds) {
        if (memberIds == null || memberIds.isEmpty() || memberIds.size() > 50) {
            return Result.failed("member ID不能为空，且不能超过50个");
        }
        LambdaQueryWrapper<UmsMember> wrapper = Wrappers.<UmsMember>lambdaQuery().in(UmsMember::getId, memberIds);
        List<UmsMember> list = memberService.list(wrapper);

        return Result.success(list);
    }

    @ApiOperation(value = "修改")
    @PutMapping(value = "/{id}")
    public <T> Result<T> update(
            @PathVariable Long id,
            @RequestBody UmsMember member) {
        boolean status = memberService.updateById(member);
        return Result.judge(status);
    }

    @ApiOperation(value = "修改状态")
    @PatchMapping("/{id}")
    public <T> Result<T> patch(@PathVariable Long id, @RequestBody UmsMember user) {
        LambdaUpdateWrapper<UmsMember> updateWrapper = new LambdaUpdateWrapper<UmsMember>().eq(UmsMember::getId, id);
        updateWrapper.set(user.getStatus() != null, UmsMember::getStatus, user.getStatus());
        boolean status = memberService.update(updateWrapper);
        if (status) {
            memberService.feng(id,user.getStatus());
        }
        return Result.judge(status);
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("/{ids}")
    public <T> Result<T> delete(@PathVariable String ids) {
        boolean status = memberService.update(new LambdaUpdateWrapper<UmsMember>()
                .in(UmsMember::getId, Arrays.asList(ids.split(",")))
                .set(UmsMember::getDeleted, STATUS_YES));
        return Result.judge(status);
    }


    /**
     * 导出用户
     *
     * @param response
     */
    @ApiOperation(value = "导出用户")
    @GetMapping("/export")
    public void export(HttpServletResponse response,
                       @ApiParam(value = "开始时间") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime started,
                       @ApiParam(value = "结束时间") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime ended) throws IOException {
        try {
            LambdaQueryWrapper<UmsMember> wrapper = Wrappers.lambdaQuery();
            wrapper.ge(started != null, UmsMember::getCreated, started);
            wrapper.le(ended != null, UmsMember::getCreated, ended);
            wrapper.orderByDesc(UmsMember::getId);
            wrapper.orderByDesc(UmsMember::getUpdated);
            //
            IPage<UmsMember> result = memberService.page(new Page<>(1, 5000), wrapper);
            List<MemberExportVO> list = new ArrayList<>();

            if (ValidateUtil.isNotEmpty(result.getRecords())) {
                for (UmsMember umsMember : result.getRecords()) {
                    list.add(BeanUtil.copy(umsMember, MemberExportVO.class).setStatus(StatusEnum.getLabel(umsMember.getStatus()))
                            .setAuthStatus(MemberAuthStatusEnum.getLabel(umsMember.getAuthStatus())));
                }
            }

            ExcelExportUtil.exportCsv(response, MemberHeader.headers(), list, "用户导出-" + DateUtil.format(new Date(), YYYY_MM_DD_HH_MM_SS) + ".csv");
        } catch (Exception e) {
            response.reset();
        }
    }
}
