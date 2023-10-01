package com.muling.mall.pms.controller.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.enums.VisibleEnum;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.mall.pms.pojo.entity.PmsSubject;
import com.muling.mall.pms.pojo.query.SubjectPageQuery;
import com.muling.mall.pms.service.IPmsSubjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.Arrays;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系列控制器
 */
@Api(tags = "app-系列")
@RestController
@RequestMapping("/app-api/v1/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final IPmsSubjectService subjectService;

    @ApiOperation(value = "系列分页列表")
    @GetMapping("/page")
    public PageResult<PmsSubject> list(SubjectPageQuery queryParams) {
        LambdaQueryWrapper<PmsSubject> queryWrapper = new LambdaQueryWrapper<PmsSubject>()
                .like(StringUtils.isNotBlank(queryParams.getName()), PmsSubject::getName, queryParams.getName())
                .in(!Arrays.isNullOrEmpty(queryParams.getBrandIds()), PmsSubject::getBrandId, queryParams.getBrandIds())
                .eq(queryParams.getSubjectId() != null, PmsSubject::getId, queryParams.getSubjectId())
                .eq(PmsSubject::getVisible, VisibleEnum.DISPLAY)
                .orderByDesc(PmsSubject::getVisible)
                .orderByDesc(PmsSubject::getSort);
        IPage<PmsSubject> result = subjectService.page(new Page(queryParams.getPageNum(), queryParams.getPageSize()), queryWrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "系列详情")
    @GetMapping("/{subjectId}/detail")
    public Result<PmsSubject> detail(@ApiParam("系列ID") @PathVariable Long subjectId) {
        PmsSubject pmsSubject = subjectService.getById(subjectId);
        return Result.success(pmsSubject);
    }

}
