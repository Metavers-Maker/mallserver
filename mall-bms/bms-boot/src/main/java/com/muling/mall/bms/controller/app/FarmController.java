
package com.muling.mall.bms.controller.app;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.muling.common.annotation.AutoLog;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.common.web.annotation.RequestLimit;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.bms.pojo.dto.ClaimDTO;
import com.muling.mall.bms.pojo.dto.StakeDTO;
import com.muling.mall.bms.pojo.dto.WithdrawDTO;
import com.muling.mall.bms.pojo.query.admin.StakePageQuery;
import com.muling.mall.bms.pojo.query.app.FarmClaimPageQuery;
import com.muling.mall.bms.pojo.query.app.FarmLogPageQuery;
import com.muling.mall.bms.pojo.vo.app.FarmClaimVO;
import com.muling.mall.bms.pojo.vo.app.FarmLogVO;
import com.muling.mall.bms.pojo.vo.app.FarmPoolVO;
import com.muling.mall.bms.service.IFarmPoolService;
import com.muling.mall.bms.service.IFarmService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

//@Api(tags = "app-挖矿管理")
@RestController
@RequestMapping("/app-api/v1/farm")
@RequiredArgsConstructor
public class FarmController {

    private final IFarmPoolService stakePoolService;

    private final IFarmService farmService;

    @ApiOperation(value = "列表")
    @GetMapping("/page")
    public PageResult<FarmPoolVO> list(StakePageQuery queryParams) {

        IPage<FarmPoolVO> page = stakePoolService.page(queryParams);
        return PageResult.success(page);
    }

    @ApiOperation(value = "质押")
    @PostMapping("/stake")
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    @RequestLimit(waits = 2, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result stake(@RequestBody StakeDTO stakeDTO) {
        Long memberId = MemberUtils.getMemberId();
        boolean status = farmService.stake(memberId, stakeDTO);
        return Result.judge(status);
    }

    @ApiOperation(value = "提取")
    @PostMapping("/withdraw")
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    @RequestLimit(waits = 2, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result withdraw(@RequestBody WithdrawDTO withdrawDTO) {
        Long memberId = MemberUtils.getMemberId();
        boolean status = farmService.withdraw(memberId, withdrawDTO);
        return Result.judge(status);
    }

    @ApiOperation(value = "奖励列表")
    @PostMapping("/claim/page")
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    @RequestLimit(waits = 2, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public PageResult<FarmClaimVO> claimPage(@RequestBody FarmClaimPageQuery pageQuery) {
        Long memberId = MemberUtils.getMemberId();
        IPage<FarmClaimVO> page = farmService.claimPage(memberId, pageQuery);
        return PageResult.success(page);
    }

    @ApiOperation(value = "奖励")
    @PostMapping("/claim")
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    @RequestLimit(waits = 2, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result claim(@RequestBody ClaimDTO claimDTO) {
        Long memberId = MemberUtils.getMemberId();
        boolean status = farmService.claim(memberId, claimDTO);
        return Result.judge(status);
    }

    @ApiOperation(value = "日志列表")
    @PostMapping("/log/page")
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    @RequestLimit(waits = 2, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public PageResult<FarmLogVO> logPage(@RequestBody FarmLogPageQuery queryParams) {
        Long memberId = MemberUtils.getMemberId();
        IPage<FarmLogVO> page = farmService.logPage(memberId, queryParams);
        return PageResult.success(page);
    }
}
