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
import com.muling.mall.farm.pojo.vo.app.FarmMemberVO;
import com.muling.mall.farm.service.IFarmMemberService;
import com.muling.mall.farm.service.IFarmService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


@Api(tags = "app-农场管理")
@RestController
@RequestMapping("/app-api/v1/farm")
@RequiredArgsConstructor
public class FarmController {

    private final IFarmService farmService;

    private final IFarmMemberService farmMemberService;

    private final RestTemplate restTemplate;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "农场ID") Long farmId

    ) {
        Long memberId = MemberUtils.getMemberId();
        LambdaQueryWrapper<FarmMember> queryWrapper = new LambdaQueryWrapper<FarmMember>()
                .eq(FarmMember::getMemberId, memberId)
                .eq(farmId != null, FarmMember::getFarmId, farmId)
                .orderByDesc(FarmMember::getUpdated);
        Page<FarmMember> page = farmMemberService.page(new Page<>(pageNum, pageSize), queryWrapper);
        Page<FarmMemberVO> result = FarmMemberConverter.INSTANCE.entity2PageVO(page);

        return PageResult.success(result);
    }

    @ApiOperation("开启")
    @PostMapping("/open")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result open(@RequestBody FarmOpenForm form) {

        //先检测用户是否被封禁
        farmService.checkUser();

        //
        boolean verify = BehaviorUtil.verify(restTemplate, form.getValidate(), "");
        if (!verify) {
            throw new BizException(ResultCode.REQUEST_INVALID, "行为验证无效");
        }
        boolean result = farmService.open(form.getFarmId());
        return Result.judge(result);
    }


    @ApiOperation("奖励")
    @PostMapping("/claim")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result claim(@RequestParam Long farmId) {
        //先检测用户是否被封禁
        farmService.checkUser();
        //
        boolean result = farmService.claim(farmId);
        return Result.judge(result);
    }

}
