package com.muling.mall.bms.controller.admin;

import com.muling.common.annotation.AutoLog;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.result.Result;
import com.muling.mall.bms.pojo.dto.CompoundDTO;
import com.muling.mall.bms.service.IMemberItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = "admin-合成")
@RestController("CompoundController")
@RequestMapping("/api/v1/compound")
@RequiredArgsConstructor
public class CompoundController {

    private final IMemberItemService memberItemService;

    @ApiOperation(value = "物品合成")
    @PostMapping("/{memberId}")
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    public Result compound( @PathVariable("memberId") Long memberId, @RequestBody CompoundDTO compoundDTO) {
        boolean status = memberItemService.compound(memberId, compoundDTO);
        return Result.judge(status);
    }
}
