package com.muling.mall.bms.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.annotation.AutoLog;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.mall.bms.pojo.entity.BmsAirdropItem;
import com.muling.mall.bms.pojo.entity.OmsMemberItem;
import com.muling.mall.bms.pojo.form.AirdropItemForm;
import com.muling.mall.bms.service.IBmsAirdropItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Api(tags = "admin-空投活动任务")
@RestController("AirdropItemController")
@RequestMapping("/api/v1/airdrop/item")
@RequiredArgsConstructor
public class AirdropItemController {

    private final IBmsAirdropItemService airdropItemService;

    @ApiOperation(value = "空投任务列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "活动Id", example = "0") Long airdropId,
            @ApiParam(value = "用户Id", example = "0") Long memberId,
            @ApiParam(value = "SpuId", example = "0") Long spuId,
            @ApiParam(value = "活动状态", example = "0") Integer status) {

        LambdaQueryWrapper<BmsAirdropItem> queryWrapper = new LambdaQueryWrapper<BmsAirdropItem>()
                .eq(airdropId != null, BmsAirdropItem::getAirdropId, airdropId)
                .eq(memberId != null, BmsAirdropItem::getMemberId, memberId)
                .eq(spuId != null, BmsAirdropItem::getSpuId, spuId)
                .eq(status != null, BmsAirdropItem::getStatus, status)
                .orderByDesc(BmsAirdropItem::getUpdated);
        Page<BmsAirdropItem> result = airdropItemService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "新增空投任务")
    @PostMapping
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    public Result add(
            @RequestBody AirdropItemForm airdropItemForm) {
        boolean status = airdropItemService.save(airdropItemForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "修改空投任务")
    @PutMapping(value = "/{id}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result updateById(
            @ApiParam("活动id") @PathVariable Long id,
            @RequestBody AirdropItemForm airdropItemForm) {

        boolean status = airdropItemService.updateById(id, airdropItemForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "删除空投任务")
    @DeleteMapping("/{ids}")
    @AutoLog(operateType = LogOperateTypeEnum.DELETE, logType = LogTypeEnum.OPERATE)
    public Result delete(
            @ApiParam("id集合") @PathVariable("ids") String ids) {
        boolean status = airdropItemService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.judge(status);
    }

    @ApiOperation(value = "执行空投")
    @PutMapping("/exec/{id}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result exec(@ApiParam("活动id") @PathVariable Long id) {
        boolean ret = airdropItemService.exec(id);
        return Result.judge(ret);
    }
    //
}
