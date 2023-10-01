package com.muling.mall.bms.controller.admin;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.annotation.AutoLog;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.common.util.DateUtil;
import com.muling.common.util.ValidateUtil;
import com.muling.common.web.util.ExcelExportUtil;
import com.muling.mall.bms.constant.ItemHeader;
import com.muling.mall.bms.dto.MemberItemDTO;
import com.muling.mall.bms.pojo.dto.MemberItemExportDTO;
import com.muling.mall.bms.pojo.entity.OmsMemberItem;
import com.muling.mall.bms.pojo.form.admin.BatchSourceForm;
import com.muling.mall.bms.pojo.form.admin.ItemTransferAdminForm;
import com.muling.mall.bms.pojo.query.admin.ItemPageQuery;
import com.muling.mall.bms.service.IBmsBsnService;
import com.muling.mall.bms.service.IMemberItemService;
import com.muling.mall.pms.api.SpuFeignClient;
import com.muling.mall.pms.pojo.dto.SpuInfoDTO;
import com.muling.mall.pms.protocol.SpuListByIdsRequest;
import com.muling.mall.ums.api.MemberFeignClient;
import com.muling.mall.ums.pojo.dto.MemberDTO;
import com.muling.mall.ums.pojo.dto.MemberListByIds;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.muling.common.util.DateUtil.YYYY_MM_DD_HH_MM_SS;

@Api(tags = "admin-会员物品管理")
@RestController("ItemController")
@RequestMapping("/api/v1/items")
@Slf4j
@AllArgsConstructor
public class ItemController {

    private final IMemberItemService memberItemService;
    private final IBmsBsnService bsnService;
    private final MemberFeignClient memberFeignClient;
    private final SpuFeignClient spuFeignClient;

    @ApiOperation("分页列表")
    @GetMapping
    public PageResult page(ItemPageQuery queryParams) {
        LambdaQueryWrapper<OmsMemberItem> wrapper = Wrappers.<OmsMemberItem>lambdaQuery()
                .eq(queryParams.getMemberId() != null, OmsMemberItem::getMemberId, queryParams.getMemberId())
                .like(StrUtil.isNotBlank(queryParams.getName()), OmsMemberItem::getName, queryParams.getName())
                .eq(queryParams.getSpuId() != null, OmsMemberItem::getSpuId, queryParams.getSpuId())
                .eq(queryParams.getFreezeType() != null, OmsMemberItem::getFreezeType, queryParams.getFreezeType())
                .eq(queryParams.getStatus() != null, OmsMemberItem::getStatus, queryParams.getStatus())
                .eq(queryParams.getFromType() != null, OmsMemberItem::getFromType, queryParams.getFromType())
                .orderByDesc(OmsMemberItem::getUpdated);
        IPage<OmsMemberItem> result = memberItemService.page(new Page(queryParams.getPageNum(), queryParams.getPageSize()), wrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "首发发布")
    @PutMapping(value = "/publish/{spuId}")
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    public Result publish(
            @ApiParam("spuId") @PathVariable Long spuId) {
        boolean status = memberItemService.publish(spuId);
        return Result.judge(status);
    }

    @ApiOperation(value = "批量设置资源图")
    @PutMapping(value = "/batch")
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    public Result batchUrl(@ApiParam("资源路径") @RequestBody BatchSourceForm sourceForm) {
        boolean status = memberItemService.batchUrl(sourceForm.getSpuId(), sourceForm.getPathUrl());
        return Result.judge(status);
    }

    @ApiOperation(value = "物品空投")
    @PutMapping(value = "/airdrop/{spuId}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result airdrop(
            @ApiParam("spuId") @PathVariable Long spuId,
            @ApiParam("用户ID") @RequestParam Long memberId,
            @ApiParam("物品数量") @RequestParam(defaultValue = "1") Integer count) {
        boolean status = memberItemService.airdrop(memberId, spuId, count, "后台发放");
        return Result.judge(status);
    }

    @ApiOperation(value = "铸造")
    @PutMapping(value = "/mint/{id}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result mintById(
            @ApiParam("id") @PathVariable Long id) {
        boolean status = bsnService.mintById(id);
        return Result.judge(status);
    }

    @ApiOperation(value = "批量铸造")
    @PutMapping(value = "/mint/batch/{spuId}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result mintBySpu(
            @ApiParam("id") @PathVariable Long spuId) {
        boolean status = bsnService.mintBySpu(spuId);
        return Result.judge(status);
    }

    @ApiOperation(value = "转移上链")
    @PutMapping(value = "/transfer/{id}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result transferById(
            @ApiParam("id") @PathVariable Long id,
            @RequestBody ItemTransferAdminForm transferForm) {

        boolean status = memberItemService.transferById(id, transferForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "转移用户的物品")
    @PutMapping(value = "/{itemId}/transfer")
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    public Result transfer(
            @ApiParam("itemId") @PathVariable Long itemId, @RequestParam Long source, @RequestParam Long target) {
        boolean status = memberItemService.transferSourceToTarget(itemId, source, target);
        return Result.judge(status);
    }


    /**
     * 导出订单
     *
     * @param response
     */
    @GetMapping("/export")
    @ApiOperation(value = "导出用户物品")
    public void export(HttpServletResponse response, ItemPageQuery queryParams) throws IOException {
        try {

            LambdaQueryWrapper<OmsMemberItem> wrapper = Wrappers.<OmsMemberItem>lambdaQuery()
                    .eq(queryParams.getMemberId() != null, OmsMemberItem::getMemberId, queryParams.getMemberId())
                    .like(StrUtil.isNotBlank(queryParams.getName()), OmsMemberItem::getName, queryParams.getName())
                    .eq(queryParams.getStatus() != null, OmsMemberItem::getStatus, queryParams.getStatus())
                    .orderByDesc(OmsMemberItem::getUpdated);
            IPage<OmsMemberItem> result = memberItemService.page(new Page(queryParams.getPageNum(), queryParams.getPageSize()), wrapper);

            List<MemberItemExportDTO> list = new ArrayList<>();
            if (ValidateUtil.isNotEmpty(result.getRecords())) {
                List<Long> memberIds = result.getRecords().stream().map(OmsMemberItem::getMemberId).collect(Collectors.toList());
                List<Long> spuIds = result.getRecords().stream().map(OmsMemberItem::getSpuId).collect(Collectors.toList());
                Result<List<MemberDTO>> memberResult = memberFeignClient.listByIds(new MemberListByIds().setMemberIds(memberIds));
                Result<List<SpuInfoDTO>> spuInfoResult = spuFeignClient.listByIds(new SpuListByIdsRequest().setSpuIds(spuIds));
                Assert.notEmpty(memberResult.getData());

                Map<Long, MemberDTO> memberId2MemberDTOMap = memberResult.getData().stream().collect(Collectors.toMap(MemberDTO::getId, Function.identity()));
                Map<Long, SpuInfoDTO> spuId2InfoMap = spuInfoResult.getData().stream().collect(Collectors.toMap(SpuInfoDTO::getId, Function.identity()));
                for (OmsMemberItem item : result.getRecords()) {
                    list.add(new MemberItemExportDTO()
                            .setName(item.getName())
                            .setItemNo(item.getItemNo())
                            .setFromType(item.getFromType().getLabel())
                            .setSpuId(item.getSpuId())
                            .setMemberId(item.getMemberId())
                            .setTransferPrice(item.getSwapPrice())
                            .setFirstPrice(spuId2InfoMap.getOrDefault(item.getSpuId(), new SpuInfoDTO()).getPrice())
                            .setTransferTime(item.getStarted())
                            .setMemberNickName(memberId2MemberDTOMap.getOrDefault(item.getMemberId(), new MemberDTO()).getNickName())
                            .setMemberMobile(memberId2MemberDTOMap.getOrDefault(item.getMemberId(), new MemberDTO()).getMobile()))
                    ;
                }
            }
            ExcelExportUtil.exportCsv(response, ItemHeader.headers(), list, "用户持仓导出" + DateUtil.format(new Date(), YYYY_MM_DD_HH_MM_SS) + ".csv");
        } catch (Exception e) {
            response.reset();
        }
    }

    //

}
