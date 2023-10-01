package com.muling.mall.farm.controller.admin;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.mall.bms.api.ItemFeignClient;
import com.muling.mall.farm.converter.FarmDuomobItemConverter;
import com.muling.mall.farm.pojo.entity.*;
import com.muling.mall.farm.pojo.form.admin.FarmDuomobForm;
import com.muling.mall.farm.pojo.vo.app.FarmRakeVO;
import com.muling.mall.farm.service.IFarmDuomobItemService;
import com.muling.mall.farm.service.IFarmRakeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.util.DigestUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Api(tags = "admin-农场Rake")
@RestController("AdminFarmRakeController")
@RequestMapping("/api/v1/farm/rake")
@RequiredArgsConstructor
public class FarmRakeController {

    private final IFarmRakeService farmRakeService;

    private final IFarmDuomobItemService farmDuomobItemService;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "会员ID") Long memberId,
            @ApiParam(value = "目标会员ID") Long targetId,
            @ApiParam(value = "会员ID") Integer status
    ) {
        LambdaQueryWrapper<FarmRake> queryWrapper = new LambdaQueryWrapper<FarmRake>()
                .eq(memberId!=null,FarmRake::getMemberId,memberId)
                .eq(targetId!=null,FarmRake::getTargetId,targetId)
                .eq(status!=null,FarmRake::getStatus,status)
                .orderByDesc(FarmRake::getUpdated);
        Page<FarmRake> result = farmRakeService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    @ApiOperation("收获")
    @GetMapping("/claim")
    public Result claim() {
        Map<Integer,FarmRakeVO> farmRakeVOMap = farmRakeService.claim();
        return Result.success(farmRakeVOMap);
    }

    @ApiOperation("DuoMob测试")
    @GetMapping("/duomob/test")
    public Result duomobTest(@ApiParam(value = "签名串", example = "") @RequestParam(required = true) String signPre) {
//        String signPre = "advert_id=1500002895&advert_name=%E4%B9%90%E4%BA%AB%E6%B8%B8&content=%E4%B9%90%E4%BA%AB%E6%B8%B8%E7%99%BB%E9%99%86%E6%B8%B8%E6%88%8F%E8%AF%95%E7%8E%A93%E5%88%86%E9%92%9F%E8%8E%B7%E5%BE%970.55%E5%85%83&created=1564640415&device_id=99001215466228&media_id=dy_59610466&media_income=0.44&member_income=0.55&order_id=127786693&user_id=100000&key=7892d05cdec7f54715d6ab85ed37bf88";
        String res = DigestUtils.md5DigestAsHex(signPre.getBytes());
        return Result.success(res);
    }

    @ApiOperation(value = "Duomob创建")
    @PostMapping("/duomob/create")
    public Result createTest(
            @RequestBody @Validated FarmDuomobForm form
    ) {
//        form.setContent(URLUtil.encodeAll(form.getContent()));
        FarmDuomobItem farmDuomobItem = FarmDuomobItemConverter.INSTANCE.formToEntity(form);
        boolean result = farmDuomobItemService.saveDTO(farmDuomobItem);
        return Result.judge(result);
    }
}
