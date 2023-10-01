package com.muling.mall.farm.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.mall.farm.pojo.entity.FarmMemberItem;
import com.muling.mall.farm.service.IFarmMemberItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@Api(tags = "admin-工作包管理")
@RestController("AdminFarmBagController")
@RequestMapping("/api/v1/farm-bag")
@RequiredArgsConstructor
public class FarmBagController {

    final IFarmMemberItemService farmMemberItemService;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "会员ID") Long memberId,
            @ApiParam(value = "名称") String name

    ) {
        LambdaQueryWrapper<FarmMemberItem> queryWrapper = new LambdaQueryWrapper<FarmMemberItem>()
                .eq(memberId != null, FarmMemberItem::getMemberId, memberId)
                .like(name!=null,FarmMemberItem::getName,name)
                .orderByDesc(FarmMemberItem::getCreated);
        Page<FarmMemberItem> result = farmMemberItemService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "刷新工作包")
    @GetMapping("/refresh")
    public Result refresh(@RequestParam @ApiParam(value = "工作包ID") Long id
    ) {
        boolean f = farmMemberItemService.refresh(id);
        return Result.judge(f);
    }

    @ApiOperation(value = "封禁工作包")
    @PutMapping("/disable")
    public Result disableByAdmin(@RequestParam @ApiParam(value = "工作包ID") Long id
    ) {
        boolean f = farmMemberItemService.disableByAdmin(id);
        return Result.judge(f);
    }

    @ApiOperation(value = "开启工作包")
    @PutMapping("/enable")
    public Result enableByAdmin(@RequestParam @ApiParam(value = "工作包ID") Long id
    ) {
        boolean f = farmMemberItemService.enableByAdmin(id);
        return Result.judge(f);
    }

    @ApiOperation(value = "关闭工作包")
    @PutMapping("/close")
    public Result closeByAdmin(@RequestParam @ApiParam(value = "工作包ID") Long id
    ) {
        boolean f = farmMemberItemService.closeByAdmin(id);
        return Result.judge(f);
    }

    @ApiOperation(value = "冻结工作包")
    @PutMapping("/freeze")
    public Result freezeByAdmin(@RequestParam @ApiParam(value = "工作包ID") Long id
    ) {
        boolean f = farmMemberItemService.freezeByAdmin(id);
        return Result.judge(f);
    }

    @ApiOperation(value = "修正工作包返佣")
    @PutMapping("/fix")
    public Result fixByAdmin(@RequestParam @ApiParam(value = "工作包ID") Long id
    ) {
        boolean f = farmMemberItemService.fixByAdmin(id);
        return Result.judge(f);
    }

}
