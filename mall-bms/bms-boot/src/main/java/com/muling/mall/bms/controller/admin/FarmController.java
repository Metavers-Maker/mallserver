package com.muling.mall.bms.controller.admin;

import com.muling.common.annotation.AutoLog;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.result.Result;
import com.muling.common.web.annotation.RequestLimit;
import com.muling.mall.bms.pojo.dto.DispatchDTO;
import com.muling.mall.bms.service.IFarmService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@Api(tags = "admin-挖矿")
@RestController("FarmController")
@RequestMapping("/api/v1/farm")
@RequiredArgsConstructor
public class FarmController {

    private final IFarmService farmService;

    @ApiOperation(value = "测试用-结算池")
    @PostMapping("/settle-pool")
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    @RequestLimit(waits = 2, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result settlePool(@RequestBody DispatchDTO dispatchDTO) {
        boolean status = farmService.settlePool(dispatchDTO.getPoolId());
        return Result.judge(status);
    }

}
