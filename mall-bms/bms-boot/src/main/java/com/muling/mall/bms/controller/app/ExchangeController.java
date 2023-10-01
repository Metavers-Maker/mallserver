
package com.muling.mall.bms.controller.app;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.common.web.annotation.RequestLimit;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.bms.constant.OmsConstants;
import com.muling.mall.bms.pojo.form.app.ExchangeForm;
import com.muling.mall.bms.pojo.query.admin.ExchangeLogPageQuery;
import com.muling.mall.bms.pojo.query.admin.ExchangePageQuery;
import com.muling.mall.bms.pojo.vo.app.ExchangeLogVO;
import com.muling.mall.bms.pojo.vo.app.ExchangeVO;
import com.muling.mall.bms.service.IExchangeConfigService;
import com.muling.mall.bms.service.IExchangeLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "app-兑换管理")
@RestController
@RequestMapping("/app-api/v1/exchange")
@RequiredArgsConstructor
public class ExchangeController {

    private final IExchangeConfigService exchangeConfigService;

    private final IExchangeLogService exchangeLogService;

    private final StringRedisTemplate redisTemplate;

    @ApiOperation(value = "列表")
    @GetMapping("/page")
    public PageResult<ExchangeVO> list(ExchangePageQuery queryParams) {

        IPage<ExchangeVO> page = exchangeConfigService.page(queryParams);
        return PageResult.success(page);
    }

    @ApiOperation(value = "日志列表")
    @GetMapping("/page/log")
    public PageResult<ExchangeLogVO> logList(ExchangeLogPageQuery queryParams) {

        IPage<ExchangeLogVO> page = exchangeLogService.page(queryParams);
        return PageResult.success(page);
    }


    @ApiOperation(value = "兑换")
    @PostMapping
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result exchange(
            @Valid @RequestBody ExchangeForm exchangeForm) {

        exchangeConfigService.exchange(exchangeForm);
        return Result.success();
    }


    @ApiOperation(value = "兑换次数")
    @GetMapping("/num")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result num(Long exchangeId) {
        Long memberId = MemberUtils.getMemberId();
        String s = redisTemplate.opsForValue().get(OmsConstants.EXCHANGE_ITEM_NUM_PREFIX + memberId + ":" + exchangeId);
        if (StrUtil.isBlank(s)) {
            return Result.success(0);
        } else {
            return Result.success(s);
        }
    }

    @ApiOperation(value = "最大兑换次数")
    @GetMapping("/max-limit")
    @RequestLimit(waits = 1, limitFiledType = RequestLimit.LimitFiledType.MEMBER_ID)
    public Result maxLimit(Long exchangeId) {

        String s = redisTemplate.opsForValue().get(OmsConstants.EXCHANGE_MAX_LIMIT_PREFIX + exchangeId);
        if (StrUtil.isBlank(s)) {
            return Result.success(0);
        } else {
            return Result.success(s);
        }
    }
}
