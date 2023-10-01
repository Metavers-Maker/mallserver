package com.muling.mall.pms.controller.app;

import com.muling.common.result.Result;
import com.muling.mall.pms.pojo.vo.BannerVO;
import com.muling.mall.pms.service.IPmsBannerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "app-轮播信息")
@RestController
@RequestMapping("/app-api/v1/banners")
@RequiredArgsConstructor
public class BannerController {

//    private final PmsBannerEService bannerEService;

    private final IPmsBannerService bannerService;

    @ApiOperation(value = "轮播列表")
    @GetMapping
    public Result<List<BannerVO>> list(@ApiParam("链接类型") Integer linktype) {
        List<BannerVO> lists = bannerService.getBannerVisibleLists(linktype);
        return Result.success(lists);
    }

}
