package com.muling.mall.bms.api;

import com.muling.common.result.Result;
import com.muling.mall.bms.dto.MarketItemDTO;
import com.muling.mall.bms.dto.MemberItemDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.web3j.abi.datatypes.Bool;

import java.util.Arrays;
import java.util.List;

/**
 * @author haoxr
 * @description TODO
 * @createTime 2021/3/13 11:59
 */
@FeignClient(value = "mall-bms", contextId = "item")
public interface ItemFeignClient {

    @GetMapping("/app-api/v1/rpc/items/{itemId}/info")
    Result<MemberItemDTO> getItemInfo(@PathVariable Long itemId);

//    @ApiOperation(value = "锁定首发市场物品")
    @PostMapping("/app-api/v1/rpc/items/publish/lock")
    Result<List<MemberItemDTO>> lockPublish(
            @RequestParam Long memberId,
            @RequestParam Long spuId,
            @RequestParam Integer count
    );

//    @ApiOperation(value = "解锁首发市场物品")
    @PostMapping("/app-api/v1/rpc/items/publish/unlock")
    Result<Boolean> unlockPublish(
            @RequestParam Long memberId,
            @RequestParam Long spuId,
            @RequestParam String itemNosStr,
            @RequestParam Boolean payResult
    );
}
