package com.muling.mall.bms.api;

import com.muling.common.result.Result;
import com.muling.mall.bms.dto.MarketItemDTO;
import com.muling.mall.bms.dto.MemberItemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.web3j.abi.datatypes.Bool;

/**
 * @author haoxr
 * @description TODO
 * @createTime 2021/3/13 11:59
 */
@FeignClient(value = "mall-bms", contextId = "market")
public interface MarketFeignClient {

    @GetMapping("/app-api/v1/rpc/market/{marketId}/info")
    Result<MarketItemDTO> getMarketInfo(@PathVariable Long marketId);

    @PostMapping("/app-api/v1/rpc/market/{marketId}/lock")
    Result<Boolean> lockMarketItem(@PathVariable Long marketId, @RequestParam Long memberId);

    @PostMapping("/app-api/v1/rpc/market/{marketId}/unlock")
    Result<Boolean> unlockMarketItem(@PathVariable Long marketId);
}
