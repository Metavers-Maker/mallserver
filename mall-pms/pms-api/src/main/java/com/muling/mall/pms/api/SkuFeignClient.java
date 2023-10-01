package com.muling.mall.pms.api;

import com.muling.common.result.Result;
import com.muling.mall.pms.pojo.dto.CheckPriceDTO;
import com.muling.mall.pms.pojo.dto.RndDTO;
import com.muling.mall.pms.pojo.dto.SkuInfoDTO;
import com.muling.mall.pms.pojo.dto.app.LockStockDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "mall-pms", contextId = "sku")
public interface SkuFeignClient {

    /**
     * 获取商品库存单元信息
     */
    @GetMapping("/app-api/v1/rpc/sku/{skuId}/info")
    Result<SkuInfoDTO> getSkuInfo(@PathVariable Long skuId);

    /**
     * 锁定商品库存
     */
    @PutMapping("/app-api/v1/rpc/sku/_lock")
    Result lockStock(@RequestBody LockStockDTO lockStockDTO);

    /**
     * 解锁商品库存
     */
    @PutMapping("/app-api/v1/rpc/sku/_unlock")
    Result unlockStock(@RequestParam String orderToken);

    /**
     * 根据订单扣减商品库存
     */
    @PutMapping("/app-api/v1/rpc/sku/_deduct")
    Result deductStock(@RequestParam String orderToken);

    /**
     * 扣减开盲盒的商品库存
     */
    @PutMapping("/app-api/v1/rpc/sku/{skuId}/_deduct/rnd")
    Result deductRndStock(@PathVariable Long skuId, @RequestParam Integer rndStockNum);
    /**
     * 订单商品验价
     *
     * @param checkPriceDTO
     */
    @PostMapping("/app-api/v1/rpc/sku/price/_check")
    Result<Boolean> checkPrice(@RequestBody CheckPriceDTO checkPriceDTO);


    /**
     * 增加商品铸造量
     */
    @PutMapping("/app-api/v1/rpc/sku/{skuId}/_mint")
    Result deductMint(@PathVariable Long skuId, @RequestParam Integer num);

    /**
     * 获取商品盲盒规则信息
     */
    @GetMapping("/app-api/v1/rpc/sku/{skuId}/rnd-info")
    public Result<List<RndDTO>> getSkuRndInfo(@PathVariable Long skuId);

    @GetMapping("/app-api/v1/rpc/sku/list")
    Result<List<SkuInfoDTO>> list(@RequestParam String begin, @RequestParam String end);
}
