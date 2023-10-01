
package com.muling.mall.otc.controller.app;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.common.web.annotation.RequestLimit;
import com.muling.mall.otc.pojo.form.PayInfoForm;
import com.muling.mall.otc.pojo.query.app.PayInfoPageQuery;
import com.muling.mall.otc.pojo.vo.PayInfoVO;
import com.muling.mall.otc.service.IPayInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Api(tags = "app-支付信息")
@RestController
@RequestMapping("/app-api/v1/pay-info")
@RequiredArgsConstructor
public class PayInfoController {


    private final IPayInfoService payInfoService;

    @ApiOperation(value = "列表")
    @GetMapping("/page")
    public PageResult<PayInfoVO> list(PayInfoPageQuery queryParams) {

        IPage<PayInfoVO> page = payInfoService.page(queryParams);
        return PageResult.success(page);
    }

    @ApiOperation(value = "新增")
    @PostMapping
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result add(
            @RequestBody PayInfoForm channelForm) {
        boolean status = payInfoService.save(channelForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "修改")
    @PutMapping(value = "/{id}")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result updateById(
            @ApiParam("id") @PathVariable Long id,
            @RequestBody PayInfoForm channelForm) {
        boolean status = payInfoService.updateById(id, channelForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("/{ids}")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result delete(
            @ApiParam("id集合") @PathVariable("ids") String ids) {
        boolean status = payInfoService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.judge(status);
    }
}
