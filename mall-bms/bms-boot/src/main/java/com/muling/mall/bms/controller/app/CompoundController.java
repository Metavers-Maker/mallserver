
package com.muling.mall.bms.controller.app;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.muling.common.annotation.AutoLog;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.common.web.annotation.RequestLimit;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.bms.pojo.dto.CompoundDTO;
import com.muling.mall.bms.pojo.query.admin.CompoundPageQuery;
import com.muling.mall.bms.pojo.vo.app.CompoundVO;
import com.muling.mall.bms.service.ICompoundConfigService;
import com.muling.mall.bms.service.IMemberItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = "app-合成管理")
@RestController
@RequestMapping("/app-api/v1/compound")
@RequiredArgsConstructor
public class CompoundController {

    private final ICompoundConfigService compoundConfigService;

    private final IMemberItemService memberItemService;

    @ApiOperation(value = "列表")
    @GetMapping("/page")
    public PageResult<CompoundVO> list(CompoundPageQuery queryParams) {

        IPage<CompoundVO> page = compoundConfigService.page(queryParams);
        return PageResult.success(page);
    }

    @ApiOperation(value = "物品合成")
    @PostMapping
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    @RequestLimit(waits = 2, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result compound(@RequestBody CompoundDTO compoundDTO) {
        Long memberId = MemberUtils.getMemberId();
        boolean status = memberItemService.compound(memberId,compoundDTO);
        return Result.judge(status);
    }
}
