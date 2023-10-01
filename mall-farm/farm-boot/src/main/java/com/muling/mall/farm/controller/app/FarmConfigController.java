package com.muling.mall.farm.controller.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.enums.VisibleEnum;
import com.muling.common.result.PageResult;
import com.muling.mall.farm.converter.FarmConfigConverter;
import com.muling.mall.farm.pojo.entity.FarmConfig;
import com.muling.mall.farm.pojo.vo.app.FarmConfigVO;
import com.muling.mall.farm.service.IFarmConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "app-农场配置")
@RestController
@RequestMapping("/app-api/v1/farm-config")
@RequiredArgsConstructor
public class FarmConfigController {

    private final IFarmConfigService farmConfigService;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public PageResult<FarmConfigVO> page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize
    ) {
        LambdaQueryWrapper<FarmConfig> queryWrapper = new LambdaQueryWrapper<FarmConfig>()
                .eq(FarmConfig::getVisible, VisibleEnum.DISPLAY)
                .orderByDesc(FarmConfig::getUpdated);
        Page<FarmConfig> page = farmConfigService.page(new Page<>(pageNum, pageSize), queryWrapper);
        Page<FarmConfigVO> result = FarmConfigConverter.INSTANCE.entity2PageVO(page);
        return PageResult.success(result);

    }

}
