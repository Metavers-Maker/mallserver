package com.muling.mall.bms.controller.app;

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

//@Api(tags = "app-会员任务")
@RestController
@RequestMapping("/app-api/v1/member-mission")
@RequiredArgsConstructor
public class MemberMissionController {

    private final IMissionConfigService missionConfigService;
    private final IMemberMissionService memberMissionService;

    @ApiOperation(value = "任务配置列表分页")
    @GetMapping("/page")
    public PageResult pageAll(@ApiParam(value = "页码", example = "1") Long pageNum,
                              @ApiParam(value = "每页数量", example = "10") Long pageSize,
                              @ApiParam(value = "任务包配置ID", example = "0") Long groupId) {
        //
        LambdaQueryWrapper<OmsMissionConfig> queryWrapper = new LambdaQueryWrapper<OmsMissionConfig>()
                .eq(OmsMissionConfig::getVisible, 1)
                .eq(groupId != null,OmsMissionConfig::getGroupId, groupId)
                .orderByDesc(OmsMissionConfig::getUpdated);
        Page<OmsMissionConfig> page = missionConfigService.page(new Page<>(pageNum, pageSize), queryWrapper);
        Page<MissionConfigVOApp> result = MissionConfigConverter.INSTANCE.entity2PageVOApp(page);
        return PageResult.success(result);
    }

    @ApiOperation(value = "个人任务列表分页")
    @GetMapping("/pageMe")
    public PageResult pageMe(@ApiParam(value = "页码", example = "1") Long pageNum,
                             @ApiParam(value = "每页数量", example = "10") Long pageSize,
                             @ApiParam(value = "任务包配置ID", example = "0") Long groupId) {
        Long memberId = MemberUtils.getMemberId();
        LambdaQueryWrapper<OmsMemberMission> queryWrapper = new LambdaQueryWrapper<OmsMemberMission>()
                .eq(OmsMemberMission::getMemberId, memberId)
                .eq(groupId != null,OmsMemberMission::getMissionGroupId, groupId)
                .orderByDesc(OmsMemberMission::getUpdated);
        Page<OmsMemberMission> page = memberMissionService.page(new Page<>(pageNum, pageSize), queryWrapper);
        Page<MemberMissionVO> result = MemberMissionConverter.INSTANCE.entity2PageVO(page);
        //
        return PageResult.success(result);
    }

    @ApiOperation(value = "申请任务")
    @PutMapping("/apply/{id}")
    public Result apply(
            @ApiParam(value = "ID") @PathVariable Long id
    ) {
        MemberMissionVO ret = memberMissionService.apply(id);
        return Result.success(ret);
    }

    @ApiOperation(value = "提交任务信息")
    @PutMapping("/submit/{id}")
    public Result submit(
            @ApiParam(value = "ID") @PathVariable Long id,
            @RequestBody @Validated MemberMissionForm form
    ) {
        boolean status = memberMissionService.submit(id,form);
        return Result.success(status);
    }

}
