package com.muling.mall.bms.controller.app;

import com.muling.common.result.Result;
import com.muling.common.protocol.SearchRequest;
import com.muling.common.web.service.ISearchService;
import com.muling.mall.bms.protocol.SearchResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Api(tags = "app-搜索")
@RestController
@RequestMapping("/app-api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final ISearchService searchService;

    @ApiOperation(value = "重新创建索引-测试用")
    @GetMapping("/recreateIndex")
    public Result recreateIndex() throws IOException {

        searchService.recreateIndex();

        return Result.success();
    }

    @ApiOperation(value = "创建索引-测试用")
    @GetMapping("/createIndex")
    public Result createIndex() throws IOException {

        searchService.createIndex();

        return Result.success();
    }

    @ApiOperation(value = "关键词搜索")
    @PostMapping
    public Result<SearchResponse> search(@ApiParam("搜索请求体") @RequestBody  SearchRequest request) {

        return searchService.search(request);
    }
}
