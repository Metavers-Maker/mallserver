package com.muling.mall.farm.controller.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.enums.VisibleEnum;
import com.muling.common.result.PageResult;
import com.muling.mall.farm.converter.FarmBagConfigConverter;
import com.muling.mall.farm.pojo.entity.FarmBagConfig;
import com.muling.mall.farm.pojo.vo.app.FarmBagConfigVO;
import com.muling.mall.farm.service.IFarmBagConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "app-农场包配置")
@RestController
@RequestMapping("/app-api/v1/bag-config")
@RequiredArgsConstructor
public class FarmBagConfigController {

    private final IFarmBagConfigService farmBagConfigService;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public PageResult<FarmBagConfigVO> page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize
    ) {
        LambdaQueryWrapper<FarmBagConfig> queryWrapper = new LambdaQueryWrapper<FarmBagConfig>()
                .eq(FarmBagConfig::getVisible, VisibleEnum.DISPLAY)
                .orderByDesc(FarmBagConfig::getUpdated);
        Page<FarmBagConfig> page = farmBagConfigService.page(new Page<>(pageNum, pageSize), queryWrapper);
        Page<FarmBagConfigVO> result = FarmBagConfigConverter.INSTANCE.entity2PageVO(page);
        return PageResult.success(result);

    }

}
