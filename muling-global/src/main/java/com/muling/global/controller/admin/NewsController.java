package com.muling.global.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.enums.VisibleEnum;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.global.pojo.entity.News;
import com.muling.global.pojo.form.NewsForm;
import com.muling.global.service.INewsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Api(tags = "admin-协议配置")
@RestController("AdminNewsController")
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
public class NewsController {

    private final INewsService newsService;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam("标题") String title,
            @ApiParam("类型") Integer type
            ) {
        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<News>()
                .eq(type != null, News::getType, type)
                .like(StrUtil.isNotBlank(title), News::getTitle, title)
                .orderByDesc(News::getSort)
                .orderByDesc(News::getUpdated);
        Page<News> result = newsService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "新增")
    @PostMapping
    public Result add(
            @RequestBody @Validated NewsForm newsForm
    ) {
        boolean result = newsService.add(newsForm);
        return Result.judge(result);
    }

    @ApiOperation(value = "修改")
    @PutMapping("/{id}")
    public Result update(
            @ApiParam(value = "ID") @PathVariable Long id,
            @RequestBody @Validated NewsForm newsForm
    ) {
        boolean result = newsService.update(id, newsForm);
        return Result.judge(result);
    }

    @ApiOperation(value = "修改显示状态")
    @PatchMapping(value = "/{id}")
    public Result display(
            @PathVariable Long id, VisibleEnum visible) {
        boolean status = newsService.update(id, visible);
        return Result.judge(status);
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("/{ids}")
    public Result deleteAddress(
            @ApiParam("IDS以英文逗号(,)分割") @PathVariable String ids
    ) {
        List<String> list = Arrays.asList(ids.split(","));
        boolean status = newsService.delete(list);
        return Result.judge(status);
    }
}
