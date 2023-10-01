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

@Api(tags = "rpc-2级市场信息")
@RestController("RpcMarketController")
@RequestMapping("/app-api/v1/rpc/market")
@RequiredArgsConstructor
public class MarketController {

    private final IMarketService marketService;

    @ApiOperation(value = "获取二级市场信息")
    @GetMapping("/{marketId}/info")
    Result<MarketItemDTO> getMarketInfo(@PathVariable Long marketId) {
        OmsMarket omsMarket = marketService.getById(marketId);
        MarketItemDTO marketItemDTO = MarketConverter.INSTANCE.po2dto(omsMarket);
        return Result.success(marketItemDTO);
    }

    @ApiOperation(value = "锁定二级市场物品")
    @PostMapping("/{marketId}/lock")
    Result<Boolean> lockMarketItem(@PathVariable Long marketId, @RequestParam Long memberId) {
        boolean ret = marketService.lockById(marketId, memberId);
        return Result.success(ret);
    }

    @ApiOperation(value = "解锁二级市场物品")
    @PostMapping("/{marketId}/unlock")
    Result<Boolean> unlockMarketItem(@PathVariable Long marketId) {
        boolean ret = marketService.unlockById(marketId);
        return Result.success(ret);
    }
    //
}
