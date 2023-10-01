package com.muling.global.controller.app;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.global.pojo.entity.GlobalConfig;
import com.muling.global.pojo.vo.GlobalConfigVO;
import com.muling.global.service.IGlobalConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "app-全局配置")
@RestController
@RequestMapping("/app-api/v1/global-config")
@RequiredArgsConstructor
public class GlobalConfigController {

    private final IGlobalConfigService globalConfigService;

    @ApiOperation(value = "获取全局配置列表")
    @GetMapping
    public Result<List<GlobalConfigVO>> list() {
        List<GlobalConfigVO> result = globalConfigService.voList();
        return Result.success(result);
    }
    
    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam("名称") String name,
            @ApiParam("类型") String type
    ) {
        LambdaQueryWrapper<GlobalConfig> queryWrapper = new LambdaQueryWrapper<GlobalConfig>()
                .eq(type!=null,GlobalConfig::getType,type)
                .like(StrUtil.isNotBlank(name), GlobalConfig::getName, name)
                .orderByDesc(GlobalConfig::getUpdated)
                .orderByDesc(GlobalConfig::getCreated);
        Page<GlobalConfig> result = globalConfigService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

}
