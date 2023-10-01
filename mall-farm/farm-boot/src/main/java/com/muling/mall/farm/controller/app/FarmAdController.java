package com.muling.mall.farm.controller.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.exception.BizException;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.common.web.annotation.RequestLimit;
import com.muling.common.web.util.BehaviorUtil;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.farm.converter.FarmMemberConverter;
import com.muling.mall.farm.pojo.entity.FarmMember;
import com.muling.mall.farm.pojo.form.app.FarmOpenForm;
import com.muling.mall.farm.pojo.vo.app.FarmAdVO;
import com.muling.mall.farm.pojo.vo.app.FarmMemberVO;
import com.muling.mall.farm.service.IFarmAdItemService;
import com.muling.mall.farm.service.IFarmAdService;
import com.muling.mall.farm.service.IFarmMemberService;
import com.muling.mall.farm.service.IFarmService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


@Api(tags = "app-农场激励视频")
@RestController
@RequestMapping("/app-api/v1/farm/ad")
@RequiredArgsConstructor
public class FarmAdController {

    private final IFarmService farmService;

    private final IFarmAdService farmAdService;

    private final IFarmAdItemService farmAdItemService;

    @ApiOperation("请求任务")
    @PostMapping("/open")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result openAd() {
        //先检测用户是否被封禁
        farmService.checkUser();
        FarmAdVO farmAdVO = farmAdService.openAd();
        return Result.success(farmAdVO);
    }

    @ApiOperation("检测是否完成")
    @GetMapping("/check")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result checkTrans( @ApiParam(value = "广告交易ID", example = "") String transId) {
        farmService.checkUser();
        boolean f = farmAdItemService.checkTrans(transId);
        return Result.judge(f);
    }

    @ApiOperation("检测进度")
    @PostMapping("/step")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result stepAd() {
        Integer result = farmAdService.stepAd();
        return Result.success(result);
    }

    @ApiOperation("领取奖励")
    @GetMapping("/reward")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result rewardAd() {
        farmService.checkUser();
        FarmAdVO farmAdVO = farmAdService.rewardAd();
        return Result.success(farmAdVO);
    }

}
