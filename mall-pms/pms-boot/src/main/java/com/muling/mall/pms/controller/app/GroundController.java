package com.muling.mall.pms.controller.app;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.muling.common.result.PageResult;
import com.muling.mall.pms.pojo.query.GroundPageQuery;
import com.muling.mall.pms.pojo.vo.GroundVO;
import com.muling.mall.pms.es.service.PmsGroundEService;
import com.muling.mall.pms.service.IPmsGroundService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "app-广场信息")
@RestController
@RequestMapping("/app-api/v1/ground")
@RequiredArgsConstructor
public class GroundController {

//    private final PmsGroundEService groundEService;

    private final IPmsGroundService groundService;

    @ApiOperation(value = "广场列表")
    @GetMapping("/page")
    public PageResult<GroundVO> page(GroundPageQuery queryParams) {
        IPage<GroundVO> page = groundService.page(queryParams);
        return PageResult.success(page);
    }
}
