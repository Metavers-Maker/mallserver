package com.muling.global.controller.app;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.muling.common.result.PageResult;
import com.muling.global.pojo.query.NewsPageQuery;
import com.muling.global.pojo.vo.NewsVO;
import com.muling.global.service.INewsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "app-协议配置")
@RestController
@RequestMapping("/app-api/v1/news")
@RequiredArgsConstructor
public class NewsController {

    private final INewsService newsService;

    @ApiOperation(value = "分页")
    @GetMapping("/page")
    public PageResult page(NewsPageQuery queryParams) {
        IPage<NewsVO> result = newsService.page(queryParams);
        return PageResult.success(result);
    }

}
