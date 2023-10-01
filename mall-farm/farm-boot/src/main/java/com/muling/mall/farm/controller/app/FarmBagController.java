package com.muling.mall.farm.controller.app;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.common.web.annotation.RequestLimit;
import com.muling.mall.farm.pojo.query.app.FarmMemberItemPageQuery;
import com.muling.mall.farm.pojo.vo.app.FarmMemberItemVO;
import com.muling.mall.farm.service.IFarmService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@Api(tags = "app-农场包管理")
@RestController
@RequestMapping("/app-api/v1/farm-bag")
@RequiredArgsConstructor
public class FarmBagController {

    final IFarmService farmService;

    @ApiOperation(value = "列表")
    @GetMapping("/page")
    public PageResult<FarmMemberItemVO> page(FarmMemberItemPageQuery queryParams) {
        Page<FarmMemberItemVO> page = farmService.page(queryParams);
        return PageResult.success(page);
    }

    @ApiOperation("激活")
    @PostMapping("/activate")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result activate(@RequestParam Long farmItemId) {
        boolean result = farmService.activate(farmItemId);
        return Result.judge(result);
    }

}
