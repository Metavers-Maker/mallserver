package com.muling.mall.wms.api;

import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.mall.wms.pojo.dto.WalletDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(value = "mall-wms", contextId = "wallet")
public interface WalletFeignClient {

    /**
     * 更新余额
     */
    @PutMapping("/app-api/v1/rpc/wallet/update")
    Result updateBalance(@RequestBody WalletDTO walletDTO);

    /**
     * 更新余额列表
     */
    @PutMapping("/app-api/v1/rpc/wallet/update/list")
    Result updateBalances(@RequestBody List<WalletDTO> list);

    /**
     * 获得指定用户的指定币种的值
     */
    @GetMapping("/app-api/v1/rpc/wallet")
    public Result<BigDecimal> getCoinValueByMemberIdAndCoinType(@RequestParam Long memberId, @RequestParam Integer coinType);

    /**
     * 封禁/解禁用户钱包
     */
    @GetMapping("/app-api/v1/rpc/wallet/feng")
    public Result<Boolean> feng(@RequestParam Long memberId, @RequestParam Integer status);

    /**
     * 获取用户积分排行榜
     */
    @GetMapping("/app-api/v1/rpc/wallet/page")
    public PageResult page(
            @RequestParam Long pageNum,
            @RequestParam Long pageSize,
            @RequestParam Integer coinType);

}
