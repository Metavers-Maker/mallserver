package com.muling.mall.pms.api;

import com.muling.common.result.Result;
import com.muling.mall.pms.pojo.dto.CheckPriceDTO;
import com.muling.mall.pms.pojo.dto.RndDTO;
import com.muling.mall.pms.pojo.dto.SkuInfoDTO;
import com.muling.mall.pms.pojo.dto.SpuInfoDTO;
import com.muling.mall.pms.pojo.dto.app.LockStockDTO;
import com.muling.mall.pms.protocol.SpuListByIdsRequest;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "mall-pms", contextId = "spu")
public interface SpuFeignClient {

    /**
     * 获取商品单元信息
     */
    @GetMapping("/app-api/v1/rpc/spu/{spuId}/info")
    Result<SpuInfoDTO> getSpuInfo(@PathVariable Long spuId);

    /**
     * 首发商品发布锁定
     */
    @PostMapping("/app-api/v1/rpc/spu/{spuId}/publish/lock")
    Result<SpuInfoDTO> publishLock(
            @ApiParam("SPU ID") @PathVariable Long spuId
    );

    /**
     * 首发商品发布解锁
     */
    @PostMapping("/app-api/v1/rpc/spu/{spuId}/publish/unLock")
    Result<Boolean> publishUnlock(
            @ApiParam("SPU ID") @PathVariable Long spuId,
            @ApiParam("是否发布成功") @RequestParam(required = true) Boolean isOk
    );

    /**
     * SPU销售变化
     */
    @PostMapping("/app-api/v1/rpc/spu/{spuId}/sale/change")
    Result<Boolean> saleChange(
            @ApiParam("SPU ID") @PathVariable Long spuId,
            @ApiParam("数量") @RequestParam(required = true) Integer count
    );

    /**
     * 订单商品验价
     */
    @PostMapping("/app-api/v1/rpc/spu/price/_check")
    Result<Boolean> checkPrice(@RequestBody CheckPriceDTO checkPriceDTO);

    /**
     * 获取商品盲盒规则信息
     */
    @GetMapping("/app-api/v1/rpc/spu/{spuId}/rnd-info")
    Result<List<RndDTO>> getSpuRndInfo(@PathVariable Long spuId);

    /**
     * 盲盒锁定目标条目
     */
    @PostMapping("/app-api/v1/rpc/spu/{spuId}/rnd/lock")
    Result<List<RndDTO>> spuRndLock(
            @ApiParam("SPU ID") @PathVariable Long spuId
    );

    /**
     * 盲盒解锁目标条目
     */
    @PostMapping("/app-api/v1/rpc/spu/{spuId}/rnd/unLock")
    Result<Boolean> spuRndUnlock(
            @ApiParam("SPU ID") @PathVariable Long spuId,
            @ApiParam("SPU ID") @RequestParam(required = true) Long rndId
    );


    /**
     * 查询会员列表
     *
     * @return
     */
    @PostMapping("/app-api/v1/rpc/spu/listByIds")
    Result<List<SpuInfoDTO>> listByIds(@RequestBody SpuListByIdsRequest request);

}
