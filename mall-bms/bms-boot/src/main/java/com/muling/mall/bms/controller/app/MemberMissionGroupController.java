package com.muling.mall.bms.controller.app;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.muling.common.exception.BizException;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.common.web.annotation.RequestLimit;
import com.muling.mall.bms.pojo.query.app.MissionGroupItemPageQuery;
import com.muling.mall.bms.pojo.vo.app.MemberMissionGroupVO;
import com.muling.mall.bms.service.IMemberMissionGroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

//@Api(tags = "app-会员任务包")
@RestController
@RequestMapping("/app-api/v1/member-mission-group")
@RequiredArgsConstructor
public class MemberMissionGroupController {

    private final IMemberMissionGroupService memberMissionGroupService;

    @ApiOperation(value = "全局任务包列表")
    @GetMapping("/page")
    public PageResult<MemberMissionGroupVO> page(MissionGroupItemPageQuery queryParams) {
        IPage<MemberMissionGroupVO> page = memberMissionGroupService.page(queryParams);
        return PageResult.success(page);
    }

    @ApiOperation("申请任务包")
    @PostMapping("/apply")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result apply(@RequestParam String missionGroudName) {
        MemberMissionGroupVO result = memberMissionGroupService.apply(missionGroudName);
        if (result == null) {
            throw new BizException(ResultCode.MISSION_GROUP_NO_EXIST);
        }
        return Result.success(result);
    }

    @ApiOperation("领取任务包奖励")
    @PostMapping("/claim")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result claim(@RequestParam Long id) {
//        boolean verify = BehaviorUtil.verify(restTemplate, form.getValidate(), "");
//        if (!verify) {
//            throw new BizException(ResultCode.REQUEST_INVALID, "行为验证无效");
//        }
        boolean result = memberMissionGroupService.claim(id);
        return Result.judge(result);
    }


}
