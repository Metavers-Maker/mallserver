package com.muling.mall.pms.controller.rpc;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.muling.common.result.Result;
import com.muling.common.util.ValidateUtil;
import com.muling.mall.pms.converter.SpuConverter;
import com.muling.mall.pms.converter.RndConverter;
import com.muling.mall.pms.pojo.dto.CheckPriceDTO;
import com.muling.mall.pms.pojo.dto.RndDTO;
import com.muling.mall.pms.pojo.dto.SpuInfoDTO;
import com.muling.mall.pms.pojo.entity.PmsRnd;
import com.muling.mall.pms.pojo.entity.PmsSpu;
import com.muling.mall.pms.protocol.SpuListByIdsRequest;
import com.muling.mall.pms.service.IPmsRndService;
import com.muling.mall.pms.service.IPmsSpuService;
import com.muling.mall.ums.pojo.dto.MemberDTO;
import com.muling.mall.ums.pojo.dto.MemberListByIds;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "rpc-商品信息")
@RestController("RpcSpuController")
@RequestMapping("/app-api/v1/rpc/spu")
@RequiredArgsConstructor
public class SpuController {

    private final IPmsSpuService spuService;
    private final IPmsRndService rndService;

    @ApiOperation(value = "商品发布锁定")
    @PostMapping("/{spuId}/publish/lock")
    public Result<SpuInfoDTO> publishLock(
            @ApiParam("SPU ID") @PathVariable Long spuId
    ) {
        SpuInfoDTO spuInfo = spuService.publishLock(spuId);
        return Result.success(spuInfo);
    }

    @ApiOperation(value = "商品发布解锁定")
    @PostMapping("/{spuId}/publish/unLock")
    public Result<Boolean> publishLock(
            @ApiParam("SPU ID") @PathVariable Long spuId,
            @ApiParam("是否发布成功") @RequestParam(required = true) Boolean isOk
    ) {
        boolean ret = spuService.publishUnlock(spuId, isOk);
        return Result.judge(ret);
    }

    @ApiOperation(value = "商品销售量变化")
    @PostMapping("/{spuId}/sale/change")
    public Result<Boolean> saleChange(
            @ApiParam("SPU ID") @PathVariable Long spuId,
            @ApiParam("数量") @RequestParam(required = true) Integer count
    ) {
        boolean ret = spuService.saleChange(spuId, count);
        return Result.judge(ret);
    }

    @ApiOperation(value = "获取商品库存信息")
    @GetMapping("/{spuId}/info")
    public Result<SpuInfoDTO> getSkuInfo(
            @ApiParam("SPU ID") @PathVariable Long spuId
    ) {
        SpuInfoDTO spuInfo = spuService.getSpuInfo(spuId);
        return Result.success(spuInfo);
    }

    @ApiOperation(value = "商品验价")
    @PostMapping("/price/_check")
    public Result<Boolean> checkPrice(@RequestBody CheckPriceDTO checkPriceDTO) {
        boolean result = spuService.checkPrice(checkPriceDTO);
        return Result.success(result);
    }

    @ApiOperation(value = "获取商品盲盒规则信息")
    @GetMapping("/{spuId}/rnd-info")
    public Result<List<RndDTO>> getSpuRndInfo(
            @ApiParam("SPU ID") @PathVariable Long spuId
    ) {
        List<PmsRnd> pmsRndList = rndService.list(Wrappers.<PmsRnd>lambdaQuery().eq(PmsRnd::getTarget, spuId));
        if (pmsRndList.isEmpty()) {
            return Result.failed("盲盒规则不存在");
        }
        List<RndDTO> rndDTOList = new ArrayList<>();
        pmsRndList.forEach(item -> {
            RndDTO dto = new RndDTO();
            dto.setId(item.getId());
            dto.setTarget(item.getTarget());
            dto.setSpuId(item.getSpuId());
            dto.setSpuCount(item.getSpuCount());
            dto.setSkuId(item.getSkuId());
            dto.setProd(item.getProd());
            dto.setCoinType(item.getCoinType());
            dto.setCoinCount(item.getCoinCount());
            rndDTOList.add(dto);
        });
        return Result.success(rndDTOList);
    }

    /**
     * 盲盒锁定目标条目
     */
    @ApiOperation(value = "锁定盲盒并增加库存")
    @PostMapping("/{spuId}/rnd/lock")
    Result<List<RndDTO>> spuRndLock(
            @ApiParam("SPU ID") @PathVariable Long spuId
    ) {
        List<RndDTO> rndDTOList = rndService.lock(spuId);
        return Result.success(rndDTOList);
    }

    /**
     * 盲盒解锁目标条目
     */
    @ApiOperation(value = "解锁定盲盒并归还库存")
    @PostMapping("/{spuId}/rnd/unLock")
    Result<Boolean> spuRndUnlock(
            @ApiParam("SPU ID") @PathVariable Long spuId,
            @ApiParam("SPU ID") @RequestParam(required = true) Long rndId
    ) {
        boolean ret = rndService.unlock(spuId, rndId);
        return Result.judge(ret);
    }


    /**
     * 查询spu列表
     *
     * @return
     */
    @PostMapping("/listByIds")
    public Result<List<SpuInfoDTO>> listByIds(@RequestBody SpuListByIdsRequest request){
        Assert.notNull(request.getSpuIds());
        List<PmsSpu> spus = spuService.list(new LambdaQueryWrapper<PmsSpu>().in(PmsSpu::getId, request.getSpuIds()));

        if (ValidateUtil.isNotEmpty(spus)) {
            return Result.success(spus.stream().map(SpuConverter.INSTANCE::po2dto).collect(Collectors.toList()));
        }
        return Result.success();
    }

}
