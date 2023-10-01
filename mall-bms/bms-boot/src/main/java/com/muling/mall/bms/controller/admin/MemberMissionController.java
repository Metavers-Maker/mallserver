package com.muling.mall.bms.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.bms.converter.MemberMissionConverter;
import com.muling.mall.bms.converter.MissionConfigConverter;
import com.muling.mall.bms.pojo.entity.OmsMemberMission;
import com.muling.mall.bms.pojo.entity.OmsMissionConfig;
import com.muling.mall.bms.pojo.form.app.MemberMissionForm;
import com.muling.mall.bms.pojo.vo.app.MissionConfigVOApp;
import com.muling.mall.bms.pojo.vo.app.MemberMissionVO;
import com.muling.mall.bms.service.IMemberMissionService;
import com.muling.mall.bms.service.IMissionConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

//@Api(tags = "admin-会员任务")
@RestController("MemberMissionController")
@RequestMapping("/api/v1/member-mission")
@RequiredArgsConstructor
public class MemberMissionController {

    private final IMissionConfigService missionConfigService;
    private final IMemberMissionService memberMissionService;

    @ApiOperation(value = "任务列表分页")
    @GetMapping("/page")
    public PageResult pageAll(@ApiParam(value = "页码", example = "1") Long pageNum,
                              @ApiParam(value = "每页数量", example = "10") Long pageSize,
                              @ApiParam(value = "用户ID", example = "0") Long memberId,
                              @ApiParam(value = "任务包配置ID", example = "0") Long groupId,
                              @ApiParam(value = "任务状态", example = "0") Integer status
                              ) {
        LambdaQueryWrapper<OmsMemberMission> queryWrapper = new LambdaQueryWrapper<OmsMemberMission>()
                .eq(memberId != null,OmsMemberMission::getMemberId, memberId)
                .eq(groupId != null,OmsMemberMission::getMissionGroupId, groupId)
                .eq(status != null,OmsMemberMission::getStatus, status)
                .orderByDesc(OmsMemberMission::getCreated);
        Page<OmsMemberMission> page = memberMissionService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(page);
    }

    @ApiOperation(value = "任务审核")
    @PutMapping("/check")
    public Result check(
            @ApiParam(value = "ID") Long id,
            @ApiParam(value = "任务状态", example = "2") Integer state
    ) {
        boolean status = memberMissionService.check(id,state);
        return Result.success(status);
    }

}
