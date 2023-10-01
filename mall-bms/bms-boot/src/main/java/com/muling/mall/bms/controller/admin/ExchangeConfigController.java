package com.muling.mall.bms.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muling.common.annotation.AutoLog;
import com.muling.common.enums.LogOperateTypeEnum;
import com.muling.common.enums.LogTypeEnum;
import com.muling.common.result.PageResult;
import com.muling.common.result.Result;
import com.muling.common.web.util.MemberUtils;
import com.muling.mall.bms.constant.OmsConstants;
import com.muling.mall.bms.enums.StatusEnum;
import com.muling.mall.bms.enums.ViewTypeEnum;
import com.muling.mall.bms.pojo.entity.OmsExchangeConfig;
import com.muling.mall.bms.pojo.form.admin.ExchangeConfigForm;
import com.muling.mall.bms.service.IExchangeConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Api(tags = "admin-兑换配置")
@RestController("ExchangeConfigController")
@RequestMapping("/api/v1/exchange-config")
@RequiredArgsConstructor
public class ExchangeConfigController {

    private final IExchangeConfigService exchangeConfigService;
    private final StringRedisTemplate redisTemplate;

    private final RabbitTemplate rabbitTemplate;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public PageResult page(
            @ApiParam(value = "页码", example = "1") Long pageNum,
            @ApiParam(value = "每页数量", example = "10") Long pageSize,
            @ApiParam(value = "备注") String remark
    ) {
        LambdaQueryWrapper<OmsExchangeConfig> queryWrapper = new LambdaQueryWrapper<OmsExchangeConfig>()
                .like(StrUtil.isNotBlank(remark), OmsExchangeConfig::getRemark, remark)
                .orderByDesc(OmsExchangeConfig::getUpdated)
                .orderByDesc(OmsExchangeConfig::getCreated);
        Page<OmsExchangeConfig> result = exchangeConfigService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResult.success(result);
    }

    @ApiOperation(value = "详情")
    @GetMapping("/{id}")
    public Result getById(@ApiParam("id") @PathVariable Integer id) {
        OmsExchangeConfig config = exchangeConfigService.getById(id);
        return Result.success(config);
    }

    @ApiOperation(value = "新增")
    @PostMapping
    @AutoLog(operateType = LogOperateTypeEnum.ADD, logType = LogTypeEnum.OPERATE)
    public Result add(
            @RequestBody ExchangeConfigForm configForm) {
        boolean status = exchangeConfigService.save(configForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "修改")
    @PutMapping(value = "/{id}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result updateById(
            @ApiParam("id") @PathVariable Long id,
            @RequestBody ExchangeConfigForm configForm) {
        boolean status = exchangeConfigService.updateById(id, configForm);
        return Result.judge(status);
    }

    @ApiOperation(value = "可用")
    @PutMapping("/enable")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result enable(
            @ApiParam("ids") @RequestParam(required = true) List<Long> ids) {
        boolean status = exchangeConfigService.update(new LambdaUpdateWrapper<OmsExchangeConfig>()
                .in(OmsExchangeConfig::getId, ids)
                .eq(OmsExchangeConfig::getStatus, StatusEnum.DISABLED)
                .set(OmsExchangeConfig::getStatus, StatusEnum.ENABLE));
        return Result.judge(status);
    }

    @ApiOperation(value = "不可用")
    @PutMapping("/disabled")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result disabled(
            @ApiParam("ids") @RequestParam(required = true) List<Long> ids) {
        boolean status = exchangeConfigService.update(new LambdaUpdateWrapper<OmsExchangeConfig>()
                .in(OmsExchangeConfig::getId, ids)
                .eq(OmsExchangeConfig::getStatus, StatusEnum.ENABLE)
                .set(OmsExchangeConfig::getStatus, StatusEnum.DISABLED));
        return Result.judge(status);
    }

    @ApiOperation(value = "显示")
    @PutMapping("/display")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result display(
            @ApiParam("ids") @RequestParam(required = true) List<Long> ids) {
        boolean status = exchangeConfigService.update(new LambdaUpdateWrapper<OmsExchangeConfig>()
                .in(OmsExchangeConfig::getId, ids)
                .eq(OmsExchangeConfig::getVisible, ViewTypeEnum.INVISIBLE)
                .set(OmsExchangeConfig::getVisible, ViewTypeEnum.VISIBLE));
        return Result.judge(status);
    }

    @ApiOperation(value = "隐藏")
    @PutMapping("/hide")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result hide(
            @ApiParam("ids") @RequestParam(required = true) List<Long> ids) {
        boolean status = exchangeConfigService.update(new LambdaUpdateWrapper<OmsExchangeConfig>()
                .in(OmsExchangeConfig::getId, ids)
                .eq(OmsExchangeConfig::getVisible, ViewTypeEnum.VISIBLE)
                .set(OmsExchangeConfig::getVisible, ViewTypeEnum.INVISIBLE));
        return Result.judge(status);
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("/{ids}")
    @AutoLog(operateType = LogOperateTypeEnum.DELETE, logType = LogTypeEnum.OPERATE)
    public Result delete(
            @ApiParam("id集合") @PathVariable("ids") String ids) {
        boolean status = exchangeConfigService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.judge(status);
    }

    @ApiOperation(value = "设置最大限制")
    @PutMapping("/max-limit/{id}")
    @AutoLog(operateType = LogOperateTypeEnum.EDIT, logType = LogTypeEnum.OPERATE)
    public Result maxLimit(
            @PathVariable("id") Long id, @RequestParam(required = true) Integer maxLimit) {
        boolean status = exchangeConfigService.update(new LambdaUpdateWrapper<OmsExchangeConfig>()
                .eq(OmsExchangeConfig::getId, id)
                .set(OmsExchangeConfig::getMaxLimit, maxLimit));
        return Result.judge(status);
    }

    @ApiOperation(value = "兑换次数")
    @GetMapping("/num")
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
    public Result maxLimit(Long exchangeId) {

        String s = redisTemplate.opsForValue().get(OmsConstants.EXCHANGE_MAX_LIMIT_PREFIX + exchangeId);
        if (StrUtil.isBlank(s)) {
            return Result.success(0);
        } else {
            return Result.success(s);
        }
    }

    @ApiOperation(value = "[测试使用]-分发多模块消息")
    @GetMapping("/dispatch")
    public Result dispatch(
            @ApiParam("用户物品ID") @RequestParam Long itemId) {

        rabbitTemplate.convertAndSend("fanout.item.exchange", "", itemId);
        return Result.success();
    }
}
