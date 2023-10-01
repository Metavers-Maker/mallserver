package com.muling.mall.bms.controller.rpc;

import com.muling.common.result.Result;
import com.muling.mall.bms.converter.MarketConverter;
import com.muling.mall.bms.converter.MemberItemConverter;
import com.muling.mall.bms.dto.MarketItemDTO;
import com.muling.mall.bms.dto.MemberItemDTO;
import com.muling.mall.bms.pojo.entity.OmsMarket;
import com.muling.mall.bms.pojo.entity.OmsMemberItem;
import com.muling.mall.bms.service.IMarketService;
import com.muling.mall.bms.service.IMemberItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Api(tags = "rpc-用户物品信息")
@RestController("RpcItemController")
@RequestMapping("/app-api/v1/rpc/items")
@RequiredArgsConstructor
public class ItemController {

    private final IMemberItemService memberItemService;

    @ApiOperation(value = "获取用户物品信息")
    @GetMapping("/{itemId}/info")
    public Result<MemberItemDTO> getItemInfo(
            @ApiParam("itemId") @PathVariable Long itemId
    ) {
        OmsMemberItem memberItem = memberItemService.getById(itemId);
        MemberItemDTO memberItemDTO = MemberItemConverter.INSTANCE.po2dto(memberItem);
        return Result.success(memberItemDTO);
    }

    @ApiOperation(value = "锁定首发市场物品")
    @PostMapping("/publish/lock")
    public Result<List<MemberItemDTO>> lockPublish(
            @ApiParam("用户ID") @RequestParam Long memberId,
            @ApiParam("spuId") @RequestParam Long spuId,
            @ApiParam("数量") @RequestParam Integer count
    ) {
        List<MemberItemDTO> memberItemDTOList = memberItemService.lockPublish(memberId, spuId, count);
        return Result.success(memberItemDTOList);
    }

    @ApiOperation(value = "解锁首发市场物品")
    @PostMapping("/publish/unlock")
    public Result<Boolean> unlockPublish(
            @ApiParam("用户ID") @RequestParam Long memberId,
            @ApiParam("spuId") @RequestParam Long spuId,
            @ApiParam("物品ID") @RequestParam String itemNosStr,
            @ApiParam("支付结果") @RequestParam Boolean payResult
    ) {
        List<String> itemNos = Arrays.asList(itemNosStr.split(","));
        Boolean ret = memberItemService.unlockPublish(memberId, spuId, itemNos, payResult);
        return Result.judge(ret);
    }

}
