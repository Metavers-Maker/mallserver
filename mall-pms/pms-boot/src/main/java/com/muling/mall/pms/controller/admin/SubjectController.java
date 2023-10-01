package com.muling.mall.pms.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.annotation.AutoLog;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.mall.pms.common.enums.StatusEnum;
import com.muling.mall.pms.common.enums.ViewTypeEnum;
import com.muling.mall.pms.pojo.entity.PmsSubject;
import com.muling.mall.pms.pojo.entity.PmsSubjectConfig;
import com.muling.mall.pms.pojo.form.SubjectFormDTO;
import com.muling.mall.pms.pojo.query.SubjectPageQuery;
import com.muling.mall.pms.repository.PmsSubjectRepository;
import com.muling.mall.pms.service.IPmsSubjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.Arrays;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "admin-系列管理")
@RestController("SubjectController")
@RequestMapping("/api/v1/subject")
@Slf4j
@AllArgsConstructor
public class SubjectController {

    private final PmsSubjectRepository subjectRepository;
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;
    private IPmsSubjectService subjectService;

    @ApiOperation(value = "分页列表")
    @GetMapping("/page")
    public PageResult<PmsSubject> list(SubjectPageQuery queryParams) {
        LambdaQueryWrapper<PmsSubject> queryWrapper = new LambdaQueryWrapper<PmsSubject>()
                .like(StringUtils.isNotBlank(queryParams.getName()), PmsSubject::getName, queryParams.getName())
                .in(!Arrays.isNullOrEmpty(queryParams.getBrandIds()), PmsSubject::getBrandId, queryParams.getBrandIds())
                .orderByDesc(PmsSubject::getVisible)
                .orderByDesc(PmsSubject::getSort);
        IPage<PmsSubject> result = subjectService.page(new Page(queryParams.getPageNum(), queryParams.getPageSize()), queryWrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "详细信息")
    @GetMapping("/{subjectId}")
    public Result<PmsSubject> getById(
            @PathVariable Long subjectId) {
        PmsSubject subject = subjectService.getById(subjectId);
        return Result.success(subject);
    }

    @ApiOperation(value = "创建")
    @PostMapping
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    public Result add(@Valid @RequestBody SubjectFormDTO subjectForm) {
        PmsSubject subject = subjectService.save(subjectForm);
        return Result.success(subject);
    }

    @ApiOperation(value = "更新")
    @PutMapping(value = "/{subjectId}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public <T> Result<T> update(
            @PathVariable Long subjectId,
            @Valid @RequestBody SubjectFormDTO subjectForm) {
        boolean status = subjectService.updateById(subjectId, subjectForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "显示")
    @PutMapping("/display")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result display(
            @ApiParam("subject ids") @RequestParam(required = true) List<Long> subjectIds) {
        boolean status = subjectService.update(new LambdaUpdateWrapper<PmsSubject>()
                .in(PmsSubject::getId, subjectIds)
                .set(PmsSubject::getVisible, ViewTypeEnum.VISIBLE));
        return Result.judge(status);
    }

    @ApiOperation(value = "隐藏")
    @PutMapping("/hide")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result hide(
            @ApiParam("subject ids") @RequestParam(required = true) List<Long> subjectIds) {
        boolean status = subjectService.update(new LambdaUpdateWrapper<PmsSubject>()
                .in(PmsSubject::getId, subjectIds)
                .set(PmsSubject::getVisible, ViewTypeEnum.INVISIBLE));
        return Result.judge(status);
    }


    @ApiOperation(value = "删除")
    @DeleteMapping("/{ids}")
    @AutoLog(operateType = LogOperateTypeEnum.DELETE, logType = LogTypeEnum.OPERATE)
    public Result delete(
            @ApiParam("id集合") @PathVariable("ids") String ids) {
        boolean status = subjectService.remove(new LambdaQueryWrapper<PmsSubject>()
                .in(PmsSubject::getId, ids.split(",")));
        return Result.judge(status);
    }

    @ApiOperation(value = "这是个数据解密测试方法")
    @PutMapping(value = "/decode")
    public Result decode(@RequestBody SubjectFormDTO subjectForm) {

        return Result.success(subjectForm);
    }
}
