package com.muling.mall.farm.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.mall.farm.pojo.entity.FarmMember;
import com.muling.mall.farm.pojo.entity.FarmMemberItem;
import com.muling.mall.farm.pojo.entity.FarmRake;
import com.muling.mall.farm.service.IFarmAdService;
import com.muling.mall.farm.service.IFarmMemberService;
import com.muling.mall.farm.service.IFarmService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;


@Api(tags = "admin-农场和Ad【测试】")
@RestController("AdminFarmController")
@RequestMapping("/api/v1/farm")
@RequiredArgsConstructor
public class FarmController {

    final IFarmService farmService;

    final IFarmMemberService farmMemberService;
    final IFarmAdService farmAdService;

    final RabbitTemplate rabbitTemplate;

    @ApiOperation(value = "用户农场列表分页")
    @GetMapping("/member/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "会员ID") Long memberId,
            @ApiParam(value = "农场状态") Integer status,
            @ApiParam(value = "农场收益排序") Integer orderClaim
    ) {
        LambdaQueryWrapper<FarmMember> queryWrapper = new LambdaQueryWrapper<FarmMember>()
                .eq(memberId!=null,FarmMember::getMemberId,memberId)
                .eq(status!=null,FarmMember::getStatus,status)
                .orderByDesc(orderClaim!=null,FarmMember::getClaimCoinValue)
                .orderByDesc(FarmMember::getCreated);
        Page<FarmMember> result = farmMemberService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    @ApiOperation("创建")
    @PostMapping("/create")
    public Result create(@RequestParam Long itemId) {
        farmService.create(itemId);
        return Result.success();
    }

    @ApiOperation("关闭")
    @PostMapping("/close")
    public Result close() {
        Page<FarmMemberItem> farmMemberItemPage = farmService.closeFarmBagPage(1, 10);
        farmService.close(farmMemberItemPage.getRecords());
        return Result.success();
    }

    @ApiOperation("打开农场")
    @PostMapping("/farm/open")
    public Result openFarm(@RequestParam Long[] memberIds) {
        boolean f = farmService.openFarm(memberIds);
        return Result.judge(f);
    }

    @ApiOperation("关闭农场")
    @PostMapping("/farm/close")
    public Result closeFarm(@RequestParam Long[] memberIds) {
        boolean f = farmService.closeFarm(memberIds);
        return Result.judge(f);
    }

    @ApiOperation("重新领取农场奖励")
    @PostMapping("/farm/reclaim")
    public Result reClaim(@RequestParam Long farmId) {
        boolean f = farmService.reClaim(farmId);
        return Result.judge(f);
    }

    @ApiOperation("重置")
    @PostMapping("/reset")
    public Result reset(@ApiParam("Member Ids") @RequestParam(required = true) List<Long> memberIds,
                        @ApiParam("Farm Id") @RequestParam(required = true) Long farmId) {
        boolean f = farmService.reset(memberIds,farmId);
        return Result.judge(f);
    }

    @ApiOperation("重置所有Farm")
    @PostMapping("/reset/all")
    public Result resetFarmAll() {
        //
        try {
            Integer pageNum = 1;
            Integer pageSize = 3;
            Page<FarmMember> page = farmService.pageFarm(pageNum, pageSize);
            if (page.getRecords().size() > 0) {
                farmService.resetFarms(page.getRecords());
                while (page.hasNext()) {
                    pageNum = pageNum + 1;
                    page = farmService.pageFarm(pageNum, pageSize);
                    farmService.resetFarms(page.getRecords());
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
//            XxlJobHelper.log("-------closeFarmBagHandler end--------- ");
        }
        return Result.judge(true);
    }

    @ApiOperation("农场总产出")
    @GetMapping("/claim/total")
    public Result claimTotal() {
        //
        try {
            BigDecimal totalClamin = BigDecimal.ZERO;
            Integer pageNum = 1;
            Integer pageSize = 1000;
            Page<FarmMember> page = farmService.pageFarm(pageNum, pageSize);
            if (page.getRecords().size() > 0) {
                for(FarmMember farmMember:page.getRecords()) {
                    totalClamin = totalClamin.add(farmMember.getClaimCoinValue());
                }
                while (page.hasNext()) {
                    pageNum = pageNum + 1;
                    page = farmService.pageFarm(pageNum, pageSize);
                    for(FarmMember farmMember:page.getRecords()) {
                        totalClamin = totalClamin.add(farmMember.getClaimCoinValue());
                    }
                }
            }
            return Result.success(totalClamin);
        } catch (Exception e) {
            throw e;
        } finally {
//            XxlJobHelper.log("-------closeFarmBagHandler end--------- ");
        }
    }

    @ApiOperation("增加Ad阶段")
    @PutMapping("/ad/step")
    public Result stepAdd(@ApiParam(value = "流水号", example = "") @RequestParam(required = true) Long adSn) {
        //
        boolean f = farmAdService.stepAdGo(adSn);
        return Result.judge(f);
    }

}
