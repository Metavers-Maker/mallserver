package com.muling.mall.pms.controller.app;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.mall.pms.pojo.query.BrandPageQuery;
import com.muling.mall.pms.pojo.vo.BrandVO;
import com.muling.mall.pms.es.service.PmsBrandEService;
import com.muling.mall.pms.service.IPmsBannerService;
import com.muling.mall.pms.service.IPmsBrandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "app-作者信息")
@RestController
@RequestMapping("/app-api/v1/brand")
@RequiredArgsConstructor
public class BrandController {

//    private final PmsBrandEService brandEService;

    private final IPmsBrandService brandService;

    @ApiOperation(value = "作者列表")
    @GetMapping
    public Result<List<BrandVO>> list(
            @ApiParam("品牌ID数组") @RequestParam(required = true) List<Long> brandIds) {
        if (brandIds == null || brandIds.isEmpty() || brandIds.size() > 20) {
            return Result.failed("品牌ID不能为空，且不能超过20个");
        }
        List<BrandVO> result = brandService.getAppBrandDetails(brandIds);
        return Result.success(result);
    }

    @ApiOperation(value = "作者列表页")
    @GetMapping("/page")
    public PageResult<BrandVO> page(BrandPageQuery queryParams) {
        IPage<BrandVO> page = brandService.page(queryParams);
        return PageResult.success(page);
    }

}
